package colin29.memoriesofsword.game.match;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Draws outlines around graphics to hint to the user that they are interactable
 * 
 * E.g: Playable cards in hand, or followers that can attack.
 */
public class OutlineRenderer {

	ShapeRenderer shapeRenderer;

	public OutlineRenderer(ShapeRenderer shapeRenderer) {
		this.shapeRenderer = shapeRenderer;
	}

	Logger logger = LoggerFactory.getLogger(this.getClass());

	Map<Actor, Color> subjects = new LinkedHashMap<Actor, Color>();

	/**
	 * If subject is already present, will just set the color
	 */
	public void startDrawingMyOutline(Actor actor, Color color) {
		subjects.put(actor, color);
	}

	/**
	 * If subject is already present, will just set the color.
	 * 
	 * Uses the default color red.
	 */
	public void startDrawingMyOutline(Actor actor) {
		startDrawingMyOutline(actor, Color.RED);
	}

	/**
	 * If subject is present, removes the subject from the list to be drawn.
	 * 
	 * @param actor
	 */
	public void stopDrawingMyOutline(Actor actor) {
		removeSubject(actor);
	}

	/**
	 * Same as stopDrawingMyOutline, but for internal use.
	 * 
	 * @param actor
	 */
	void removeSubject(Actor actor) {
		subjects.remove(actor);
	}

	public void renderOutlines() {

		shapeRenderer.begin(ShapeType.Line);
		for (Actor subject : subjects.keySet()) {
			shapeRenderer.setColor(subjects.get(subject));
			Vector2 coords = subject.localToStageCoordinates(new Vector2(0, 0));
			shapeRenderer.rect(coords.x, coords.y, subject.getWidth(), subject.getHeight());
		}
		shapeRenderer.end();

	}

	public int getSubjectCount() {
		return subjects.size();
	}
}
