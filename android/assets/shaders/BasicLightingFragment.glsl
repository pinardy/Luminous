#version 300 es

precision mediump float;

in vec4 v_color;
in vec2 v_texCoords;
in vec2 v_lightPos;
in vec4 v_position;

out vec4 FragColor;

uniform sampler2D u_texture;

void main() {

    //dealing with in-game coordinates
    float dropoffDist = 50.0;
    float dist = 0.0;

    //distance btw light and actual vertex
    dist = pow(abs(v_lightPos.x - v_position.x) , 2.0);
    dist += pow(abs(v_lightPos.y - v_position.y) , 2.0);
    dist = sqrt(dist);

    //scaling from dist to maximum dropOffDist
    //the further away, the darker it gets
    FragColor = v_color*texture(u_texture, v_texCoords) * (dropoffDist / max(1.0,dist));
}
