package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.shune.xenthor_racas.cliente.TeclasXenthor;
import net.shune.xenthor_racas.efeito.RegistroEfeitos;
import net.shune.xenthor_racas.efeito.RegistroPocoes;
import net.shune.xenthor_racas.rede.RedeXenthor;
import net.shune.xenthor_racas.voo.RegistroItens;

@Mod(ModPrincipal.ID_MOD)
public class ModPrincipal {

    public static final String ID_MOD                  = "xenthor_racas";
    public static final String TAG_CLASSE              = ID_MOD + ":classe_jogador";
    public static final String TAG_ELEMENTO            = ID_MOD + ":elemento_jogador";
    public static final String TAG_RACA                = ID_MOD + ":raca_jogador";
    public static final String TAG_AGUARDANDO_ELEMENTO = ID_MOD + ":aguardando_elemento";

    public static final DeferredRegister<CreativeModeTab> ABAS_CRIATIVAS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID_MOD);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ABA_XENTHOR =
            ABAS_CRIATIVAS.register("aba_xenthor", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.xenthor_racas"))
                            .withTabsBefore(CreativeModeTabs.COMBAT)
                            .build());

    public ModPrincipal(IEventBus barramentoMod, ModContainer containerMod) {
        ABAS_CRIATIVAS.register(barramentoMod);
        RedeXenthor.registrar(barramentoMod);
        RegistroItens.registrar(barramentoMod);
        RegistroEfeitos.registrar(barramentoMod);
        NeoForge.EVENT_BUS.register(this);
        RegistroPocoes.registrar(barramentoMod);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            barramentoMod.addListener(TeclasXenthor::registrarTeclas);
        }
    }

    @SubscribeEvent
    public void aoRegistrarComandos(RegisterCommandsEvent evento) {
        ComandoClasse.registrar(evento.getDispatcher());
        ComandoClasse.registrarElemento(evento.getDispatcher());
        ComandoRaca.registrar(evento.getDispatcher());
        ComandoAnalise.registrar(evento.getDispatcher(), "shune");
        ComandoAnalise.registrar(evento.getDispatcher(), "xenthor");
    }

    @SubscribeEvent
    public void aoJogadorClonar(PlayerEvent.Clone evento) {
        if (!(evento.getEntity() instanceof ServerPlayer novo)) return;
        var antigo = evento.getOriginal().getPersistentData();
        var dados = novo.getPersistentData();

        for (String chave : antigo.getAllKeys()) {
            if (chave.startsWith(ID_MOD + ":")) {
                dados.put(chave, antigo.get(chave).copy());
            }
        }
    }

    @SubscribeEvent
    public void aoJogadorRespawnar(PlayerEvent.PlayerRespawnEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        restaurarRaca(jogador);
        restaurarClasse(jogador);
    }

    @SubscribeEvent
    public void aoJogadorEntrar(PlayerEvent.PlayerLoggedInEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        restaurarRaca(jogador);
        restaurarClasse(jogador);
    }

    public static void restaurarRaca(ServerPlayer jogador) {
        if (!LicencaRacas.isLicencaValida()) return;
        String racaSalva = jogador.getPersistentData().getString(TAG_RACA);

        if (racaSalva == null || racaSalva.isEmpty()) {
            AtributosRaca.aplicarRaca(jogador, Raca.HUMANO);
            EscalaJogador.aplicarEscala(jogador, Raca.HUMANO);
            jogador.getPersistentData().putString(TAG_RACA, Raca.HUMANO.id);
            net.shune.xenthor_racas.rede.RedeXenthor.enviarRaca(jogador, Raca.HUMANO.id);
            return;
        }

        Raca raca = Raca.porId(racaSalva);
        if (raca == null) return;

        AtributosRaca.aplicarRaca(jogador, raca);
        EscalaJogador.aplicarEscala(jogador, raca);
        net.shune.xenthor_racas.rede.RedeXenthor.enviarRaca(jogador, raca.id);

        switch (raca) {
            case CELESTIAL  -> MonitorCelestial.aplicarEquipeGlowing(jogador);
            case CORROMPIDO -> MonitorCorrompido.aplicarEquipeGlowing(jogador);
            case TRITAO     -> MonitorTritao.aplicarEquipeGlowing(jogador);
            case FADA       -> MonitorFada.aplicarEquipeGlowing(jogador);
            case ANDROID    -> MonitorAndroid.aplicarEquipeGlowing(jogador);
            case DRAGONIC   -> MonitorDragonic.aplicarEquipeGlowing(jogador);
            case MORTO_VIVO -> MonitorMortoVivo.aplicarEquipeGlowing(jogador);
            case VAMPIRO     -> MonitorVampiro.aplicarEquipeGlowing(jogador);
            case DAMPIRO     -> MonitorVampiro.aplicarEquipeGlowing(jogador);
            case AMALDICOADO -> MonitorAmaldicoado.aplicarEquipeGlowing(jogador);
            case ESPIRITO    -> MonitorEspirito.aplicarEquipeGlowing(jogador);
            default -> {}
        }
    }

    private static void restaurarClasse(ServerPlayer jogador) {
        String classeSalva = jogador.getPersistentData().getString(TAG_CLASSE);

        if (classeSalva == null || classeSalva.isEmpty()) {
            enviarMensagemEscolhaClasse(jogador);
            return;
        }

        ClasseRaca classe = ClasseRaca.porId(classeSalva);
        if (classe == null) return;

        if (classe == ClasseRaca.MAGO) {
            String elementoSalvo = jogador.getPersistentData().getString(TAG_ELEMENTO);
            ElementoMago elemento = ElementoMago.porId(elementoSalvo);
            if (elemento != null) {
                AtributosClasse.aplicarMagoComElemento(jogador, elemento);
                return;
            }
        }

        AtributosClasse.aplicarClasse(jogador, classe);
    }

    private static void enviarMensagemEscolhaClasse(ServerPlayer jogador) {
        jogador.sendSystemMessage(Component.empty());
        jogador.sendSystemMessage(
                Component.literal("⚔ Xenthor SMP ⚔")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        jogador.sendSystemMessage(
                Component.literal("Você ainda não escolheu uma classe!")
                        .withStyle(ChatFormatting.YELLOW));
        jogador.sendSystemMessage(
                Component.literal("Use o comando abaixo para jogar no seu estilo:")
                        .withStyle(ChatFormatting.GRAY));

        MutableComponent botao = Component.literal("[/classes <classe>]")
                .withStyle(style -> style
                        .withColor(0x55FFFF)
                        .withBold(true)
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/classes " + jogador.getName().getString() + " "
                        ))
                );

        jogador.sendSystemMessage(botao);
        jogador.sendSystemMessage(Component.empty());
    }
}