package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * A helper data collection for OutlineRenderer
 * 
 * In the UI, many of the actors/graphics we are drawing outlines around are short-lived. They are discarded whenever the UI view is regenerated. When
 * that happens, OutlineRenderer needs to know to stop rendering their outline. This class performs cleanup duty when actors are discarded (removed
 * from this collection)
 * 
 * @author Colin Ta
 *
 */
public class OutlineSmartArrayList<T extends Actor> extends ArrayList<T> {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OutlineRenderer outlineRenderer;

	public OutlineSmartArrayList(OutlineRenderer outlineRenderer) {
		this.outlineRenderer = outlineRenderer;
	}

	@Override
	public void clear() {
		for (Actor actor : this) {
			outlineRenderer.removeSubject(actor);
		}
		super.clear();
	}

	@Override
	public boolean remove(Object o) {
		if (contains(o)) {
			outlineRenderer.removeSubject((Actor) o);
		}
		return super.remove(o);
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeIf(Predicate<? super T> filter) {
		throw new UnsupportedOperationException();
	}

}
