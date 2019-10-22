package tilegame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import graphics.*;
import tilegame.sprites.*;


/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class ResourceManager {

    private ArrayList tiles;
    private int currentMap = 0;
    private GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite musicSprite;
    private Sprite starSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite flySprite;
    public String imgExt = ".png";

    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
    }
//
//
//    public ResourceManager() {
//        //this.gc = gc;			// don't worry about graphics for now
//        loadTileImages();
//        loadCreatureSprites();
//        loadPowerUpSprites();
//    }


    /**
        Gets an image from the images/ directory.
    */
    public Image loadImage(String name) {
	String filename = "images/" + name;
        //String filename = "images/" + name;

            File file = new File(filename);
            if (!file.exists()) {
                System.out.println("Image file could not be opened: " + filename);
            }
            else
                System.out.println("Image file opened: " + filename);

        return new ImageIcon(filename).getImage();
    }


    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }


    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }


    private Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }


    public TileMap loadNextMap() {
        TileMap map = null;
        while (map == null) {
            currentMap++;
   	    String mapFile = "maps/map" + currentMap + ".txt";
            try {		
		map = loadMap(mapFile);
/*
                map = loadMap(
                    "maps/map" + currentMap + ".txt");
*/
            }
            catch (IOException ex) {
		System.out.println ("Could not find map to load " + mapFile);
                if (currentMap == 1) {
                    // no maps to load!
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

        return map;
    }


    public TileMap reloadMap() {
        try {
            return loadMap(
                "maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, starSprite, x, y);
                }
                else if (ch == '!') {
                    addSprite(newMap, musicSprite, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, grubSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, flySprite, x, y);
                }
            }
        }

        // add the player to the map
        Sprite player = (Sprite)playerSprite.clone();
        player.setX(TileMapRenderer.tilesToPixels(3));
        player.setY(0);
        newMap.setPlayer(player);

        return newMap;
    }


    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList();
        char ch = 'A';
        while (true) {
            String path = "environment/";
            String name = path + "tile_" + ch + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) {
                System.out.println("Image file could not be opened: " + name);
                break;
            }
            else
            System.out.println("Image file opened: " + name);
                        tiles.add(loadImage(name));
            ch++;
        }
    }

    public void loadImages(Image[] images, String path){

        for (int i = 0; i < images.length; i++) {
            try {
                images[i] = loadImage(path + i + imgExt);
            }
            catch (Exception e){
                System.out.println(e);
            }
        }// end for
    }

    public void flipImages(Image[] flipped, Image[] og){
        for (int i = 0; i < og.length; i++) {
            flipped[i] = getFlippedImage(og[i]);
        }
    }

    public void mirrorImages(Image[] mirrored, Image[] og){
        for (int i = 0; i < og.length; i++) {
            mirrored[i] = getMirrorImage(og[i]);
        }
    }

    private void loadPlayer(){
        // note: all the player images are facing right!

        // load idle images for player
        // has 4 images, images/player/idle/1.png
        int numIdle = 4;
        String playerIdlePath = "player/idle/";
        Image[] playerIdle = new Image[numIdle];
        Image[] playerIdleLeft = new Image[numIdle];
        loadImages(playerIdle, playerIdlePath);
        mirrorImages(playerIdleLeft, playerIdle); // this function will mirror the images in the second array, into the first array
        // load run images for player
        int numRun = 6;
        String playerRunPath = "player/run/";
        Image[] playerRun = new Image[numRun];
        Image[] playerRunLeft = new Image[numRun];
        loadImages(playerRun, playerRunPath);
        mirrorImages(playerRunLeft, playerRun);
        // load run images for player
        int numDie = 7;
        String playerDiePath = "player/die/";
        Image[] playerDie = new Image[numDie];
        Image[] playerDieLeft = new Image[numDie];
        loadImages(playerDie, playerDiePath);
        mirrorImages(playerDieLeft, playerDie);
        // now make animations
        Animation playerIdleAnim = new Animation();
        playerIdleAnim = createPlayerAnim(playerIdle);

        Animation playerIdleLeftAnim = new Animation();
        playerIdleLeftAnim = createPlayerAnim(playerIdleLeft);

        Animation playerRunAnim = new Animation();
        playerRunAnim = createPlayerAnim(playerRun);

        Animation playerRunLeftAnim = new Animation();
        playerRunLeftAnim = createPlayerAnim(playerRunLeft);

        Animation playerDieAnim = new Animation();
        playerDieAnim = createPlayerAnim(playerDie);

        Animation playerDieLeftAnim = new Animation();
        playerDieLeftAnim = createPlayerAnim(playerDieLeft);

        playerSprite = new Player(
                playerRunLeftAnim, playerRunAnim,
                playerDieLeftAnim, playerDieAnim,
                playerIdleAnim, playerIdleLeftAnim
        );
    }

    private void loadFly(){
        // note: all the fly images are facing left
        int num = 3;
        String path = "fly/";
        Image[] flyLeft = new Image[num];
        Image[] fly = new Image[num];
        Image[] flyFlippedLeft = new Image[num]; // deadleft
        Image[] flyFlipped = new Image[num]; // dead right

        loadImages(flyLeft, path); // load initial fly, which face left
        mirrorImages(fly, flyLeft); // get right facing fly
        flipImages(flyFlippedLeft, flyLeft); // get dead fly
        flipImages(flyFlipped, fly); // get dead fly

        Animation flyAnim = new Animation();
        flyAnim = createFlyAnim(fly);
        Animation flyLeftAnim = new Animation();
        flyLeftAnim = createFlyAnim(flyLeft);
        Animation dieAnim = new Animation();
        dieAnim = createFlyAnim(flyFlipped);
        Animation dieLeftAnim = new Animation();
        dieLeftAnim = createFlyAnim(flyFlippedLeft);

        flySprite = new Fly(flyLeftAnim, flyAnim, dieLeftAnim, dieAnim);
    }

    private void loadGrub(){
        // note: all the fly images are facing left
        int num = 2;
        String path = "grub/";
        Image[] flyLeft = new Image[num];
        Image[] fly = new Image[num];
        Image[] flyFlippedLeft = new Image[num]; // deadleft
        Image[] flyFlipped = new Image[num]; // dead right

        loadImages(flyLeft, path); // load initial fly, which face left
        mirrorImages(fly, flyLeft); // get right facing fly
        flipImages(flyFlippedLeft, flyLeft); // get dead fly
        flipImages(flyFlipped, fly); // get dead fly

        Animation flyAnim = new Animation();
        flyAnim = createGrubAnim(fly);
        Animation flyLeftAnim = new Animation();
        flyLeftAnim = createGrubAnim(flyLeft);
        Animation dieAnim = new Animation();
        dieAnim = createGrubAnim(flyFlipped);
        Animation dieLeftAnim = new Animation();
        dieLeftAnim = createGrubAnim(flyFlippedLeft);

        grubSprite = new Grub(flyLeftAnim, flyAnim, dieLeftAnim, dieAnim);
    }

    public void loadCreatureSprites() {
        // TODO: 22-Oct-19 refactor this, 2D arrays SUCK!

        loadPlayer(); // check death anim
        loadFly();
        loadGrub();
        // TODO: 22-Oct-19 loadDio(); 
        // TODO: 22-Oct-19 make minions
        System.out.println("loadCreatureSprites successfully executed.");

    }

    private Animation createPlayerAnim(Image[] images)
    {
        Animation anim = new Animation();
        for (int i = 0; i < images.length; i++) {
            anim.addFrame(images[i], 200);
        }
        return anim;
    }


    private Animation createFlyAnim(Image[] images)
    {
        Animation anim = new Animation();
        for (int i = 0; i < images.length; i++) {
            anim.addFrame(images[i], 50);
        }
        return anim;
    }


    private Animation createGrubAnim(Image[] images)
    {
        Animation anim = new Animation();
        for (int i = 0; i < images.length; i++) {
            anim.addFrame(images[i], 250);
        }
        return anim;
    }

    private void loadGoalSprite(){
        String path = "drops/goal/";
        int num = 3;
        Animation anim = new Animation();
        for (int i = 0; i < num; i++) anim.addFrame(loadImage(path + i + imgExt), 150);
        goalSprite = new PowerUp.Goal(anim);
    }
    private void loadStarSprite(){
        String path = "drops/star/";
        int num = 4;
        Animation anim = new Animation();
        for (int i = 0; i < num; i++) anim.addFrame(loadImage(path + i + imgExt), 150);
        starSprite = new PowerUp.Star(anim);
    }
    private void loadMusicSprite(){
        String path = "drops/music/";
        int num = 3;
        Animation anim = new Animation();
        for (int i = 0; i < num; i++) anim.addFrame(loadImage(path + i + imgExt), 150);
        musicSprite = new PowerUp.Music(anim);
    }

    private void loadPowerUpSprites() {
        // create "goal" sprite
        loadGoalSprite();
        loadMusicSprite();
        loadStarSprite();
        System.out.println("loadPowerUpSprites successfully executed.");
    }

}
