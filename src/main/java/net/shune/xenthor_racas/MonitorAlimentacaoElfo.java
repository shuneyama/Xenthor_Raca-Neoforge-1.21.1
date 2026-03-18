package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Set;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorAlimentacaoElfo {

    private static final Set<Item> ALIMENTOS_PERMITIDOS = Set.of(
            Items.APPLE,
            Items.GOLDEN_APPLE,
            Items.ENCHANTED_GOLDEN_APPLE,
            Items.MELON_SLICE,
            Items.SWEET_BERRIES,
            Items.GLOW_BERRIES,
            Items.CARROT,
            Items.GOLDEN_CARROT,
            Items.POTATO,
            Items.BAKED_POTATO,
            Items.BEETROOT,
            Items.BEETROOT_SOUP,
            Items.MUSHROOM_STEW,
            Items.SUSPICIOUS_STEW,
            Items.COD,
            Items.COOKED_COD,
            Items.SALMON,
            Items.COOKED_SALMON,
            Items.TROPICAL_FISH,
            Items.BREAD,
            Items.CAKE,
            Items.COOKIE,
            Items.PUMPKIN_PIE,
            Items.HONEY_BOTTLE,
            Items.CHORUS_FRUIT,
            Items.DRIED_KELP
    );

    private static final Set<String> CROPTOPIA_BLOQUEADOS = Set.of(
            // carnes e derivados
            "croptopia:beef_jerky", "croptopia:pork_jerky", "croptopia:cooked_bacon",
            "croptopia:raw_bacon", "croptopia:bacon", "croptopia:carnitas",
            "croptopia:ham_sandwich", "croptopia:blt", "croptopia:pepperoni",
            "croptopia:lemon_chicken", "croptopia:fried_chicken", "croptopia:chicken_and_noodles",
            "croptopia:chicken_and_dumplings", "croptopia:chicken_and_rice", "croptopia:cashew_chicken",
            "croptopia:beef_stew", "croptopia:beef_stir_fry", "croptopia:beef_wellington",
            "croptopia:shepherds_pie", "croptopia:cornish_pasty", "croptopia:pork_and_beans",
            "croptopia:taco", "croptopia:burrito", "croptopia:fajitas", "croptopia:enchilada",
            "croptopia:tamales", "croptopia:chimichanga", "croptopia:cheeseburger",
            "croptopia:hamburger", "croptopia:supreme_pizza", "croptopia:pineapple_pepperoni_pizza",
            "croptopia:egg_roll", "croptopia:sushi", "croptopia:cooked_ravager_meat",
            "croptopia:raw_ravager_meat", "croptopia:ground_pork", "croptopia:sausage",
            "croptopia:croque_madame", "croptopia:croque_monsieur", "croptopia:the_big_breakfast",
            "croptopia:stuffed_poblanos", "croptopia:chili_relleno",
            "croptopia:fish_and_chips",
            // bebidas alcoólicas
            "croptopia:beer", "croptopia:wine", "croptopia:mead", "croptopia:rum",
            // laticínios (elfos são veganos/naturais)
            "croptopia:cheese", "croptopia:butter", "croptopia:yoghurt",
            "croptopia:grilled_cheese", "croptopia:chocolate_milkshake", "croptopia:milk_bottle",
            "croptopia:whipping_cream", "croptopia:crema",
            // ovos e pratos com ovo
            "croptopia:scrambled_eggs", "croptopia:sunny_side_eggs", "croptopia:quiche",
            "croptopia:dragon_egg_omelette", "croptopia:transcendental_breakfast",
            // pernas de rã
            "croptopia:frog_legs", "croptopia:fried_frog_legs"
    );

    @SubscribeEvent
    public static void aoClicarComItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.ELFO_NATURAL.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItemStack();
        if (!ehComida(item)) return;

        if (ehPermitido(item)) return;

        evento.setCanceled(true);
        jogador.sendSystemMessage(
                Component.literal("Elfos Naturais só podem consumir alimentos naturais!")
                        .withStyle(ChatFormatting.DARK_GREEN));
    }

    @SubscribeEvent
    public static void aoIniciarUso(LivingEntityUseItemEvent.Start evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.ELFO_NATURAL.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItem();
        if (ehComida(item) && !ehPermitido(item)) {
            evento.setCanceled(true);
        }
    }

    private static boolean ehPermitido(ItemStack item) {
        if (ALIMENTOS_PERMITIDOS.contains(item.getItem())) return true;

        String id = item.getItem().builtInRegistryHolder().key().location().toString();

        if (id.startsWith("croptopia:")) {
            return !CROPTOPIA_BLOQUEADOS.contains(id);
        }

        return id.equals("create:builders_tea");
    }

    private static boolean ehComida(ItemStack item) {
        return item.getItem().getFoodProperties(item, null) != null;
    }
}