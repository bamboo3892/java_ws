package api;

import java.io.Serializable;

/**
 * When running Learning mode, AI that implemnts this interface can learn from other AI.
 * @author bamboo3892
 */
public interface ILearnableAI {

	/**
	 * Set from past saved object read from meta file.
	 * If there is not any saved file or some errer caused, param object is set null.
	 * @param past saved object read from meta file
	 */
	public void setMetaFromSavedObject(Object o);

	/**
	 * Return whether this can learn from param AI.
	 * @param ai
	 * @return result
	 */
	public boolean canLearnFrom(AISheet ai);

	/**
	 * Learn from otherAI
	 * @param otherAI
	 * @return message
	 */
	public String learnFrom(AISheet otherAI);

	/**
	 * Be sure to return object's clone.
	 * In most case, you  should return this class's clone instance.
	 * @return object clone to be saved
	 */
	public Serializable getSaveObjectClone();

}
