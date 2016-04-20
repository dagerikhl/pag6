package no.pag6.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import no.pag6.helpers.AssetLoader;
import no.pag6.helpers.GameStateManager;
import no.pag6.states.MainMenu;
import no.pag6.states.PlayState;

public class PAG6Game extends Game {

    public static final String TAG = "PAG6Game";

    public ShapeRenderer drawer;
    public SpriteBatch spriteBatch;
    private GameStateManager gameStateManager;

    @Override
    public void create () {
        drawer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        gameStateManager = new GameStateManager(this);

        Gdx.app.log(TAG, "created");

        AssetLoader.load();

        gameStateManager.pushScreen(new PlayState(this, 1, null, "Map1.tmx")); // TODO: Set to SS when done debugging
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    @Override
    public void render() {
        super.render();
    }
    }
