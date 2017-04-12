#version 300 es

precision mediump float;

in vec2 v_texCoords;
in vec4 v_position;
in vec4 v_worldColorPillarA;
in vec4 v_worldColorPillarB;
in vec2 v_lightPosPillarA;
in vec2 v_lightPosPillarB;

out vec4 FragColor;

uniform sampler2D u_texture;

void main() {

    //dealing with in-game coordinates
    float dropoffDist = 100.0;
    float distA = 0.0;
    float distB = 0.0;

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

    //scaling from dist to maximum dropOffDist
    //the further away, the darker it gets
    FragColor = v_worldColorPillarA*texture(u_texture, v_texCoords) * (dropoffDist / max(1.0,distA)) +
                    v_worldColorPillarB*texture(u_texture, v_texCoords) * (dropoffDist / max(1.0,distB));
}
