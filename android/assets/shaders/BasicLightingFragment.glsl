#version 300 es

precision mediump float;


in vec2 v_texCoords;
in vec4 v_position;
//for player
in vec4 v_colorPlayer;
in vec2 v_lightPosPlayer;

//for pillar
in vec4 v_worldColorPillarA;
in vec4 v_worldColorPillarB;
in vec4 v_worldColorPillarC;
in vec4 v_worldColorPillarD;

in vec2 v_lightPosPillarA;
in vec2 v_lightPosPillarB;
in vec2 v_lightPosPillarC;
in vec2 v_lightPosPillarD;

out vec4 FragColor;

uniform sampler2D u_texture;

void main() {
    //For player vision
    //dealing with in-game coordinates
    float playerVision = 50.0;
    float playerDist = 0.0;
    //distance btw light and actual vertex
    playerDist = pow(abs(v_lightPosPlayer.x - v_position.x) , 2.0);
    playerDist += pow(abs(v_lightPosPlayer.y - v_position.y) , 2.0);
    playerDist = sqrt(playerDist);

    //for pillar glow
    //dealing with in-game coordinates
    float dropoffDist = 100.0;
    float distA = 0.0;
    float distB = 0.0;
    float distC = 0.0;
    float distD = 0.0;

    //PILLAR A: distance btw light and actual vertex
    distA = pow(abs(v_lightPosPillarA.x - v_position.x) , 2.0);
    distA += pow(abs(v_lightPosPillarA.y - v_position.y) , 2.0);
    distA = distA * 40.0f;
    distA = sqrt(distA);

    //PILLAR B: distance btw light and actual vertex
    distB = pow(abs(v_lightPosPillarB.x - v_position.x) , 2.0);
    distB += pow(abs(v_lightPosPillarB.y - v_position.y) , 2.0);
    distB = distB * 40.0f;
    distB = sqrt(distB);

    //PILLAR C: distance btw light and actual vertex
    distC = pow(abs(v_lightPosPillarC.x - v_position.x) , 2.0);
    distC += pow(abs(v_lightPosPillarC.y - v_position.y) , 2.0);
    distC = distC * 40.0f;
    distC = sqrt(distC);

    //PILLAR D: distance btw light and actual vertex
    distD = pow(abs(v_lightPosPillarD.x - v_position.x) , 2.0);
    distD += pow(abs(v_lightPosPillarD.y - v_position.y) , 2.0);
    distD = distD * 40.0f;
    distD = sqrt(distD);

    //scaling from dist to maximum dropOffDist
    //the further away, the darker it gets
    FragColor = v_colorPlayer*texture(u_texture, v_texCoords) * (playerVision / max(1.0,playerDist)) +
                v_worldColorPillarA*texture(u_texture, v_texCoords) * (dropoffDist / max(1.0,distA)) +
                v_worldColorPillarB*texture(u_texture, v_texCoords) * (dropoffDist / max(1.0,distB)) +
                v_worldColorPillarC*texture(u_texture, v_texCoords) * (dropoffDist / max(1.0,distC)) +
                v_worldColorPillarD*texture(u_texture, v_texCoords) * (dropoffDist / max(1.0,distD));
}
