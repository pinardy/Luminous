#version 300 es

precision mediump float;


in vec2 v_texCoords;
in vec4 v_position;
//for player
in vec4 v_colorPlayer;
in vec2 v_lightPosPlayer;
//for pillar
in vec4 v_colorPillar;
in vec2 v_lightPosPillar;

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
    float pillarDist = 0.0;
    //distance btw light and actual vertex
    pillarDist = pow(abs(v_lightPosPillar.x - v_position.x) , 2.0);
    pillarDist += pow(abs(v_lightPosPillar.y - v_position.y) , 2.0);
    pillarDist = pillarDist * 40.0f;
    pillarDist = sqrt(pillarDist);

    //scaling from dist to maximum dropOffDist
    //the further away, the darker it gets
    FragColor = v_colorPlayer*texture(u_texture, v_texCoords) * (playerVision / max(1.0,playerDist)) +
                    v_colorPillar*texture(u_texture, v_texCoords) * (glowAura / max(1.0,pillarDist));
}
