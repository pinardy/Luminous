#version 300 es

precision mediump float;


in vec2 v_texCoords;
in vec4 v_position;
//for player
in vec4 v_colorPlayer;
in vec2 v_lightPosPlayer;
//for pillar
in vec4 v_colorPillarA;
in vec2 v_lightPosPillarA;
in vec4 v_colorPillarB;
in vec2 v_lightPosPillarB;

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
    float glowAura = 100.0;
    float pillarDistA = 0.0;
    float pillarDistB = 0.0;

    //PILLAR A: distance btw light and actual vertex
    pillarDistA = pow(abs(v_lightPosPillarA.x - v_position.x) , 2.0);
    pillarDistA += pow(abs(v_lightPosPillarA.y - v_position.y) , 2.0);
    pillarDistA = pillarDistA * 40.0f;
    pillarDistA = sqrt(pillarDistA);

    //PILLAR B: distance btw light and actual vertex
    pillarDistB = pow(abs(v_lightPosPillarB.x - v_position.x) , 2.0);
    pillarDistB += pow(abs(v_lightPosPillarB.y - v_position.y) , 2.0);
    pillarDistB = pillarDistB * 40.0f;
    pillarDistB = sqrt(pillarDistB);

    //scaling from dist to maximum dropOffDist
    //the further away, the darker it gets
    FragColor = v_colorPlayer*texture(u_texture, v_texCoords) * (playerVision / max(1.0,playerDist)) +
                    v_colorPillarA*texture(u_texture, v_texCoords) * (glowAura / max(1.0,pillarDistA)) +
                    v_colorPillarB*texture(u_texture, v_texCoords) * (glowAura / max(1.0,pillarDistB));
}
