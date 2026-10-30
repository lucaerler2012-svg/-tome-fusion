package com.example.tomefusion.mixin;

import com.example.tomefusion.BookMerger;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Greift in die Ergebnis-Berechnung des Ambosses ein.
 *
 * Idee: Wenn links UND rechts ein verzaubertes Buch liegt, berechnen wir das Ergebnis selbst
 * (Level addieren) und brechen die Vanilla-Berechnung ab. In allen anderen Faellen laeuft alles
 * wie gewohnt.
 *
 * TODO (ALLES HIER): Die Namen unten stammen aus Mojang-Namen von 1.21.x. Ich konnte sie fuer 26.3
 *      nicht gegen den echten Quellcode pruefen. Wenn das Spiel beim Start mit einem Mixin-Fehler
 *      abbricht ("@Shadow field ... was not located"), den Quellcode von AnvilMenu und
 *      ItemCombinerMenu in der IDE oeffnen (./gradlew genSources) und die Namen anpassen.
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

	// --- Felder aus AnvilMenu / ItemCombinerMenu / AbstractContainerMenu ---
	// (@Shadow funktioniert auch fuer Felder der Oberklassen)

	/** Die zwei Eingabe-Slots (0 = links, 1 = rechts). */
	@Shadow @Final protected Container inputSlots;

	/** Der Ergebnis-Slot. */
	@Shadow @Final protected ResultContainer resultSlots;

	/** Die XP-Level-Kosten, die im Amboss angezeigt und beim Entnehmen abgezogen werden. */
	@Shadow @Final private DataSlot cost;

	/** Wie viele Reparatur-Materialien verbraucht werden (bei Buechern: 0). */
	@Shadow private int repairItemCountCost;

	/** Sendet Aenderungen an den Client. */
	@Shadow public abstract void broadcastChanges();

	/**
	 * TODO: Pruefen, ob die Methode in 26.3 noch "createResult" heisst und keine Parameter hat.
	 */
	@Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
	private void tomefusion$combineBooks(CallbackInfo ci) {
		ItemStack left = this.inputSlots.getItem(0);
		ItemStack right = this.inputSlots.getItem(1);

		// Nur Buch + Buch interessiert uns
		if (!left.is(Items.ENCHANTED_BOOK) || !right.is(Items.ENCHANTED_BOOK)) {
			return; // Vanilla macht weiter
		}

		// Verzauberte Buecher speichern ihre Verzauberungen in STORED_ENCHANTMENTS
		// (nicht in ENCHANTMENTS wie normale Werkzeuge!)
		ItemEnchantments leftEnchants = left.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
		ItemEnchantments rightEnchants = right.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
		if (leftEnchants.isEmpty() || rightEnchants.isEmpty()) {
			return; // Leeres Buch -> Vanilla entscheiden lassen
		}

		// Kern-Logik: addieren + begrenzen
		ItemEnchantments merged = BookMerger.merge(leftEnchants, rightEnchants);

		ItemStack result = left.copy();
		result.setCount(1);
		result.set(DataComponents.STORED_ENCHANTMENTS, merged);

		// TODO (UMBENENNEN): Vanilla erlaubt es, beim Kombinieren das Item umzubenennen
		//      (Feld "itemName" in AnvilMenu). Das fehlt hier noch. Siehe Vanilla-createResult.

		// TODO (KOSTEN): Platzhalter - siehe BookMerger.PLACEHOLDER_COST
		this.cost.set(BookMerger.PLACEHOLDER_COST);
		this.repairItemCountCost = 0;

		this.resultSlots.setItem(0, result);
		this.broadcastChanges();

		// TODO: Testen, ob die Anzeige im Amboss sofort aktualisiert wird (Client-Sync!).
		// TODO: Testen, ob das Entnehmen (onTake) beide Buecher korrekt verbraucht und die
		//       Level abzieht (Survival UND Kreativ).

		ci.cancel(); // Vanilla-Berechnung ueberspringen
	}
}
