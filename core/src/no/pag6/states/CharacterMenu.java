package no.pag6.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import no.pag6.game.PAG6Game;
import no.pag6.helpers.AssetLoader;
import no.pag6.ui.SimpleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CharacterMenu extends State {

    private int nofPlayers;
    private String nofPlayersPattern = "^[1-8]$";
    private String playerNamePattern = "^[a-zA-ZæøåÆØÅ '-]{1,15}$";
    private List<String> playerNames;
    private int currentPlayer = 0;

    // Renderers
    private GlyphLayout gl = new GlyphLayout();

    // Game objects

    // Game assets

    // Tween assets

    // Game UI
    private boolean buttonsEnabled = false;

    private List<SimpleButton> characterMenuButtons = new ArrayList<SimpleButton>();
    private SimpleButton playButton;
    private SimpleButton backButton;

    public CharacterMenu(PAG6Game game) {
        super(game);

        // Init objects and assets
        initUI();

        takeNofPlayers();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render sprites
        game.spriteBatch.setProjectionMatrix(cam.combined);
        game.spriteBatch.begin();
        game.spriteBatch.enableBlending();

        drawUI();

        game.spriteBatch.end();
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPoint.set(screenX, screenY, 0);
        projected = cam.unproject(touchPoint);
        screenX = (int) projected.x;
        screenY = (int) projected.y;

        if (buttonsEnabled) {
            backButton.isTouchDown(screenX, screenY);
            playButton.isTouchDown(screenX, screenY);
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touchPoint.set(screenX, screenY, 0);
        projected = cam.unproject(touchPoint);
        screenX = (int) projected.x;
        screenY = (int) projected.y;

        if (buttonsEnabled) {
            if (backButton.isTouchUp(screenX, screenY)) {
                game.getGameStateManager().popScreen();
            }
            if (playButton.isTouchUp(screenX, screenY)) {
                game.getGameStateManager().pushScreen(new PlayState(game, nofPlayers, playerNames, "maptest2.tmx"));
            }
        }

        return true;
    }

    private void initUI() {
        TextureRegion region;
        float regionWidth, regionHeight;

        // Buttons
        region = AssetLoader.playButtonUp;
        regionWidth = region.getRegionWidth()*UI_SCALE;
        regionHeight = region.getRegionHeight()*UI_SCALE;
        playButton = new SimpleButton(
                V_WIDTH*2/3 - regionWidth/2, V_HEIGHT/12 - regionHeight/2,
                regionWidth, regionHeight,
                AssetLoader.playButtonUp, AssetLoader.playButtonDown
        );
        characterMenuButtons.add(playButton);

        region = AssetLoader.mainMenuButtonUp;
        regionWidth = region.getRegionWidth()*UI_SCALE;
        regionHeight = region.getRegionHeight()*UI_SCALE;
        backButton = new SimpleButton(
                V_WIDTH/3 - regionWidth/2, V_HEIGHT/12 - regionHeight/2,
                regionWidth, regionHeight,
                AssetLoader.mainMenuButtonUp, AssetLoader.mainMenuButtonDown
        );
        characterMenuButtons.add(backButton);
    }

    private void takeNofPlayers() {
        // TODO: Fix something wrong with Android input
        Gdx.input.getTextInput(new Input.TextInputListener() {
            @Override
            public void input(String text) {
                if (Pattern.matches(nofPlayersPattern, text.trim())) {
                    nofPlayers = Integer.valueOf(text.trim());
                    playerNames = new ArrayList<String>();
                    for (int i = 0; i < nofPlayers; i++) {
                        playerNames.add(null);
                    }

                    takePlayerName();
                } else {
                    takeNofPlayers();
                }
            }

            @Override
            public void canceled() {
                game.getGameStateManager().popScreen();
            }
        }, "Enter number of players", "", "from 1 to 8 players");
    }

    private void takePlayerName() {
        // TODO: Fix something wrong with Android input
        Gdx.input.getTextInput(new Input.TextInputListener() {
            @Override
            public void input(String text) {
                if (Pattern.matches(playerNamePattern, text.trim())) {
                    playerNames.set(currentPlayer, text.trim().toUpperCase());
                    currentPlayer++;
                }
                System.out.println("currentPlayer = " + currentPlayer);
                if (currentPlayer < nofPlayers) {
                    takePlayerName();
                } else {
                    enableButtons();
                }
            }

            @Override
            public void canceled() {
                game.getGameStateManager().popScreen();
            }
        }, "Enter name of Player " + (currentPlayer + 1) + "/" + nofPlayers, "", "no numbers or special characters");
    }

    private void drawUI() {
        if (buttonsEnabled) {
            for (SimpleButton button : characterMenuButtons) {
                button.draw(game.spriteBatch);
            }
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arialbd.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 42;
        parameter.color = Color.BLACK;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        if (playerNames != null) {
            if (playerNames.get(nofPlayers - 1) != null) {
                String players = "PLAYERS:\n";
                for (int i = 0; i < nofPlayers; i++) {
                    players += playerNames.get(i);
                    if (i == nofPlayers - 2) {
                        players += " &";
                    } else if (i < nofPlayers - 1) {
                        players += ",";
                    }
                    if ((i + 1)%2 == 0) {
                        players += "\n";
                    } else {
                        players += " ";
                    }
                }
                players = players.trim() + ".";
                gl.setText(font, players);
                font.draw(game.spriteBatch, gl, V_WIDTH/2 - gl.width/2, V_HEIGHT*5/6);
            } else if (playerNames.size() > 0 && currentPlayer - 1 >= 0) {
                String player1NameString = "PLAYER " + (currentPlayer) + ": " + playerNames.get(currentPlayer - 1);
                gl.setText(font, player1NameString);
                font.draw(game.spriteBatch, gl, V_WIDTH/2 - gl.width/2, V_HEIGHT*5/6);
            }
        }
    }

    private void enableButtons() {
        buttonsEnabled = true;
    }

}