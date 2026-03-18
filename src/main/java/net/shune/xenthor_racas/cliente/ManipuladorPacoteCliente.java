package net.shune.xenthor_racas.cliente;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.shune.xenthor_racas.rede.*;

public class ManipuladorPacoteCliente {

    public static void aoReceberClasseEscolhida(PacoteClasseEscolhida pacote, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;

            if (pacote.silencioso()) {
                HudClasseEscolhida.ativarSilencioso(pacote.idClasse(), pacote.idElemento());
                return;
            }

            mc.level.playLocalSound(
                    mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                    SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6f, 1.0f, false);

            SomAgendado.agendar(mc, SoundEvents.AMETHYST_BLOCK_CHIME, 0.5f, 1.2f, 3);
            SomAgendado.agendar(mc, SoundEvents.AMETHYST_BLOCK_CHIME, 0.5f, 1.5f, 8);
            SomAgendado.agendar(mc, SoundEvents.EXPERIENCE_ORB_PICKUP, 0.4f, 0.8f, 14);

            HudClasseEscolhida.ativar(pacote.idClasse(), pacote.idElemento());
        });
    }

    public static void aoReceberFormaNegra(PacoteFormaNegra pacote, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            RenderElfoNegro.marcarFormaNegra(pacote.uuid(), pacote.ativa());

            mc.level.playLocalSound(
                    mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                    SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS,
                    0.5f, pacote.ativa() ? 1.2f : 0.7f, false);
        });
    }

    public static void aoReceberVooCelestial(PacoteVooCelestial pacote, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            mc.level.playLocalSound(
                    mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                    SoundEvents.PHANTOM_FLAP, SoundSource.PLAYERS,
                    0.5f, pacote.ativo() ? 1.2f : 0.8f, false);
        });
    }

    public static void aoReceberTransformacao(PacoteSyncTransformacao pacote, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            TransformacaoCache.definir(pacote.uuid(), pacote.forma());
        });
    }

    public static void aoReceberEspectral(PacoteSyncEspectral pacote, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            EspectralCache.definir(pacote.uuid(), pacote.ativo());
        });
    }

    public static void aoReceberRaca(PacoteSyncRaca pacote, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            RacaCache.definir(pacote.uuid(), pacote.racaId());
        });
    }
}