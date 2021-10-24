package com.github.afloarea.obge;

/**
 * Hybrid Engine that provides the capabilities of both {@link InteractiveObgEngine} and {@link TurnBasedObgEngine}.
 * Has a larger memory footprint that either of them.
 */
public interface MixedModeObgEngine extends InteractiveObgEngine, TurnBasedObgEngine {
}
