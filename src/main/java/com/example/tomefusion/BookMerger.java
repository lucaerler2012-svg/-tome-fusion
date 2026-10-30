package com.example.tomefusion;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * Reine Rechenlogik (kein Mixin, daher leicht testbar):
 *  - Level gleicher Verzauberungen werden ADDIERT
 *  - Ergebnis wird auf (Vanilla-Maximum x LIMIT_MULTIPLIER) begrenzt
 *  - Verzauberungen, die nur auf einem Buch vorkommen, werden uebernommen
 */
public final class BookMerger {

	/** Faktor fuer die Obergrenze: Limit = Vanilla-Max-Level x 2. */
	public static final int LIMIT_MULTIPLIER = 2;

	/**
	 * TODO (KOSTEN): Platzhalter! Hier entscheidest du spaeter, wie teuer das Kombinieren ist.
	 * Ideen: feste Kosten, Summe der Ergebnis-Level, 2^Level, Anzahl der Verzauberungen ...
	 * Beachte: Vanilla zeigt im Amboss "Zu teuer!" ab 40 Level (im Survival). Soll das weiter gelten?
	 */
	public static final int PLACEHOLDER_COST = 3;

	private BookMerger() {}

	/** Kombiniert die gespeicherten Verzauberungen zweier Buecher. */
	public static ItemEnchantments merge(ItemEnchantments left, ItemEnchantments right) {
		// Start mit allen Verzauberungen des linken Buchs
		ItemEnchantments.Mutable result = new ItemEnchantments.Mutable(left);

		for (Object2IntMap.Entry<Holder<Enchantment>> entry : right.entrySet()) {
			Holder<Enchantment> enchantment = entry.getKey();
			int rightLevel = entry.getIntValue();
			int leftLevel = result.getLevel(enchantment); // 0, falls nicht vorhanden

			if (leftLevel > 0) {
				// Gleiche Verzauberung auf beiden Buechern -> Level addieren + begrenzen
				result.set(enchantment, limit(enchantment, leftLevel + rightLevel));
			} else if (isCompatibleWithAll(enchantment, result)) {
				// Neue Verzauberung -> uebernehmen (ebenfalls begrenzen, falls rightLevel schon extrem hoch)
				result.set(enchantment, limit(enchantment, rightLevel));
			}
			// TODO: Inkompatible Verzauberungen (z. B. Schaerfe + Bann der Gliederfuessler)
			//       werden aktuell stillschweigend verworfen. Alternative: Kombination komplett
			//       verbieten (kein Ergebnis) - so wie Vanilla es bei Gegenstaenden teilweise macht.
		}
		return result.toImmutable();
	}

	/** Begrenzt ein Level auf Vanilla-Maximum x 2. */
	public static int limit(Holder<Enchantment> enchantment, int level) {
		int vanillaMax = enchantment.value().getMaxLevel();

		// TODO: Verzauberungen mit Max-Level 1 (Mending, Silk Touch, Unbreaking-Aehnliches ...):
		//       "Level 2" hat keine Wirkung / ergibt keinen Sinn. Aktuell bleiben sie auf 1.
		if (vanillaMax <= 1) {
			return 1;
		}
		int limit = vanillaMax * LIMIT_MULTIPLIER;
		return Math.min(level, limit);
	}

	private static boolean isCompatibleWithAll(Holder<Enchantment> candidate, ItemEnchantments.Mutable existing) {
		// TODO: Methodenname/Signatur pruefen - in 1.21.x war es Enchantment.areCompatible(Holder, Holder).
		//       In 26.3 koennte sich das geaendert haben (Exclusive-Sets / Tags).
		for (Holder<Enchantment> other : existing.keySet()) {
			if (!other.equals(candidate) && !Enchantment.areCompatible(other, candidate)) {
				return false;
			}
		}
		return true;
	}
}
