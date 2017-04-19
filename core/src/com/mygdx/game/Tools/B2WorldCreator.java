package com.mygdx.game.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Core;
import com.mygdx.game.Sprites.Pillar;
import com.mygdx.game.Sprites.Player;
//import com.sun.javafx.scene.control.skin.VirtualFlow;

import java.util.ArrayList;

/** This class instantiates the necessary objects in our game world
 * These objects include the Pillar and Core
 */

public class B2WorldCreator {

    // Sprites
    private Player player;
    public static ArrayList<Pillar> listOfPillars;

    public B2WorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        // body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        /* There are multiple layers created in the tmx file.
           We extract these layers out from the tmx file and instantiate
           the objects into our game world  */


        // pillar object index is 2
        int id = 0;
        listOfPillars = new ArrayList<Pillar>();
        MultiplayerGame.pillarPositions.clear();
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            MultiplayerGame.pillarPositions.add(rect);
            // instantiate a new Pillar object for its location in the map
            Pillar pillar = new Pillar(screen, rect, id);
            listOfPillars.add(pillar);
            id++;
        }

        // core object index is 3
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            MultiplayerGame.corePosition = rect;

            // instantiate a new Core object for its location in the map
            new Core(screen, rect);
        }
    }
}
