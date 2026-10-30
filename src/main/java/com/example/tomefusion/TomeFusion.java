package com.example.tomefusion;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Haupteinstiegspunkt der Mod.
 *
 * Die eigentliche Arbeit passiert nicht hier, sondern im Mixin
 * ({@link com.example.tomefusion.mixin.AnvilMenuMixin}) und in {@link BookMerger}.
 * Da die Mod keine eigenen Items/Bloecke/Netzwerkpakete hat, bleibt diese Klasse fast leer.
 */
public class TomeFusion implements ModInitializer {

	public static final String MOD_ID = "tomefusion";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Tome Fusion geladen - Buecher werden im Amboss addiert.");

		// TODO: Optional eine Config-Datei einbauen (z. B. Multiplikator 2 -> frei einstellbar,
		//       Kosten-Modus, Mod an/aus). Fuer ein Skeleton nicht noetig.
	}
}
