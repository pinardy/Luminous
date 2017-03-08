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

/**
 * Created by Pin on 06-Feb-17.
 */

public class B2WorldCreator {

    // Sprites
    private Player player;

    public B2WorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        // body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // pillar object index is 2
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            MultiplayerGame.pillarPositions.add(rect);

            // instantiate a new Pillar object for its location in the map
            new Pillar(screen, rect);

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2), (rect.getY() + rect.getHeight() / 2));

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
            fdef.shape = shape;
            fdef.filter.categoryBits = MultiplayerGame.PILLAR_BIT;
            body.createFixture(fdef);
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
