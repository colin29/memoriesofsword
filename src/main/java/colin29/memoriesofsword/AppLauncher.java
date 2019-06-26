package colin29.memoriesofsword;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class AppLauncher {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Memories of Sword";
		config.width = 960;
		config.height = 640;
		config.samples = 5;
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		new LwjglApplication(new App(), config);
	}

}
