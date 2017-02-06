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
import com.mygdx.game.Sprites.Core;
import com.mygdx.game.Sprites.Pillar;
import com.mygdx.game.Sprites.Player;

/**
 * Created by Pin on 06-Feb-17.
 */

public class B2WorldCreator {

    // Sprites
    private Player player;

    public B2WorldCreator(World world, TiledMap map){

        // body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // pillar object index is 2
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            // instantiate a new Pillar object for its location in the map
            new Pillar(world, map, rect);
        }

        // core object index is 3
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            // instantiate a new Core object for its location in the map
            new Core(world, map, rect);
        }
    }
}
