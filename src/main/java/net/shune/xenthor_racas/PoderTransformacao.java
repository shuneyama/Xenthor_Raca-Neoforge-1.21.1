package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class PoderTransformacao {

    public static final String TAG_TRANSFORMADO = ModPrincipal.ID_MOD + ":transformado";
    public static final String TAG_FORMA = ModPrincipal.ID_MOD + ":forma_atual";

    private static final ResourceLocation VIDA_MORCEGO = ResourceLocation.fromNamespaceAndPath(ModPrincipal.ID_MOD, "forma.morcego.vida");
    private static final ResourceLocation ESCALA_FORMA = ResourceLocation.fromNamespaceAndPath(ModPrincipal.ID_MOD, "forma.escala");
    private static final ResourceLocation VELOCIDADE_FORMA = ResourceLocation.fromNamespaceAndPath(ModPrincipal.ID_MOD, "forma.velocidade");

    public static void tentar(ServerPlayer jogador) {
        Raca raca = Raca.porId(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA));
        if (raca == null) return;

        if (raca == Raca.ELFO_NEGRO) {
            FormaNegra.tentar(jogador);
            return;
        }

        if (raca != Raca.VAMPIRO && raca != Raca.DAMPIRO && raca != Raca.LOBISOMEM && raca != Raca.TRITAO && raca != Raca.KITSUNE) return;

        if (jogador.getPersistentData().getBoolean(TAG_TRANSFORMADO)) {
            desativar(jogador);
            return;
        }

        String forma = switch (raca) {
            case VAMPIRO -> "morcego";
            case DAMPIRO, LOBISOMEM -> "lobo";
            case TRITAO -> "peixe";
            case KITSUNE -> "raposa";
            default -> "";
        };

        if (forma.isEmpty()) return;

        if (forma.equals("peixe") && !jogador.isInWater()) {
            jogador.sendSystemMessage(Component.literal("Você só pode se transformar em peixe dentro da água!")
                    .withStyle(ChatFormatting.AQUA));
            return;
        }

        ativar(jogador, forma, raca);
    }

    private static void ativar(ServerPlayer jogador, String forma, Raca raca) {
        jogador.getPersistentData().putBoolean(TAG_TRANSFORMADO, true);
        jogador.getPersistentData().putString(TAG_FORMA, forma);

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.INVISIBILITY, Integer.MAX_VALUE, 0, false, false), null);

        switch (forma) {
            case "morcego" -> {
                aplicarEscala(jogador, -0.70);
                aplicarVelocidade(jogador, -0.30);
                aplicarVida(jogador, -10.0);
                jogador.getAbilities().mayfly = true;
                jogador.getAbilities().setFlyingSpeed(0.03f);
                jogador.onUpdateAbilities();
            }
            case "lobo" -> {
                aplicarEscala(jogador, -0.40);
            }
            case "peixe" -> {
                aplicarEscala(jogador, -0.60);
            }
            case "raposa" -> {
                aplicarEscala(jogador, -0.50);
                aplicarVelocidade(jogador, 0.10);
            }
        }

        net.shune.xenthor_racas.rede.RedeXenthor.enviarTransformacao(jogador, forma);
        jogador.sendSystemMessage(Component.literal("Você se transformou em " + forma + "!")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }

    public static void desativar(ServerPlayer jogador) {
        String forma = jogador.getPersistentData().getString(TAG_FORMA);
        jogador.getPersistentData().putBoolean(TAG_TRANSFORMADO, false);
        jogador.getPersistentData().putString(TAG_FORMA, "");

        jogador.removeEffect(MobEffects.INVISIBILITY);

        removerModificadores(jogador);

        if ("morcego".equals(forma)) {
            jogador.getAbilities().mayfly = false;
            jogador.getAbilities().flying = false;
            jogador.getAbilities().setFlyingSpeed(0.05f);
            jogador.onUpdateAbilities();
        }

        net.shune.xenthor_racas.rede.RedeXenthor.enviarTransformacao(jogador, "");
        jogador.sendSystemMessage(Component.literal("Você voltou ao normal.")
                .withStyle(ChatFormatting.GRAY));
    }

    private static void aplicarEscala(ServerPlayer jogador, double valor) {
        AttributeInstance inst = jogador.getAttribute(Attributes.SCALE);
        if (inst != null && inst.getModifier(ESCALA_FORMA) == null) {
            inst.addTransientModifier(new AttributeModifier(ESCALA_FORMA, valor, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void aplicarVelocidade(ServerPlayer jogador, double valor) {
        AttributeInstance inst = jogador.getAttribute(Attributes.MOVEMENT_SPEED);
        if (inst != null && inst.getModifier(VELOCIDADE_FORMA) == null) {
            inst.addTransientModifier(new AttributeModifier(VELOCIDADE_FORMA, valor, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    private static void aplicarVida(ServerPlayer jogador, double valor) {
        AttributeInstance inst = jogador.getAttribute(Attributes.MAX_HEALTH);
        if (inst != null && inst.getModifier(VIDA_MORCEGO) == null) {
            inst.addTransientModifier(new AttributeModifier(VIDA_MORCEGO, valor, AttributeModifier.Operation.ADD_VALUE));
        }
        if (jogador.getHealth() > jogador.getMaxHealth()) {
            jogador.setHealth(jogador.getMaxHealth());
        }
    }

    private static void removerModificadores(ServerPlayer jogador) {
        AttributeInstance escala = jogador.getAttribute(Attributes.SCALE);
        if (escala != null) escala.removeModifier(ESCALA_FORMA);

        AttributeInstance velocidade = jogador.getAttribute(Attributes.MOVEMENT_SPEED);
        if (velocidade != null) velocidade.removeModifier(VELOCIDADE_FORMA);

        AttributeInstance vida = jogador.getAttribute(Attributes.MAX_HEALTH);
        if (vida != null) vida.removeModifier(VIDA_MORCEGO);
    }

    public static boolean estaTransformado(ServerPlayer jogador) {
        return jogador.getPersistentData().getBoolean(TAG_TRANSFORMADO);
    }

    public static String formaAtual(ServerPlayer jogador) {
        return jogador.getPersistentData().getString(TAG_FORMA);
    }

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!jogador.getPersistentData().getBoolean(TAG_TRANSFORMADO)) return;

        String forma = jogador.getPersistentData().getString(TAG_FORMA);

        switch (forma) {
            case "morcego" -> {
                if (!jogador.getAbilities().mayfly) {
                    jogador.getAbilities().mayfly = true;
                    jogador.getAbilities().setFlyingSpeed(0.03f);
                    jogador.onUpdateAbilities();
                }
            }
            case "peixe" -> {
                if (!jogador.isInWater()) {
                    if (jogador.tickCount % 20 == 0) {
                        jogador.hurt(jogador.damageSources().drown(), 4.0f);
                        jogador.sendSystemMessage(Component.literal("Você está sufocando fora da água!")
                                .withStyle(ChatFormatting.RED));
                    }
                }
            }
        }
    }
}