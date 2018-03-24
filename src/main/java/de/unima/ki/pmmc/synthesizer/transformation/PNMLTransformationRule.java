package de.unima.ki.pmmc.synthesizer.transformation;

import fr.lip6.move.pnml.pnmlcoremodel.PetriNet;

@FunctionalInterface
public interface PNMLTransformationRule extends TransformationRule{

	public PetriNet transform(PetriNet model);
}
