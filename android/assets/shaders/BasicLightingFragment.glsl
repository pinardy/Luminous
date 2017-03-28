#version 120

precision mediump float;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_lightPos;
varying vec4 v_position;

uniform sampler2D u_texture;

out vec4 FragColor;

void main() {

    //dealing with in-game coordinates
    int dropoffDist = 50;
    float dist = 0;

    //distance btw light and actual vertex
    dist = pow(abs(v_lightPos.x - v_position.x) ,2);
    dist += pow(abs(v_lightPos.y - v_position.y) ,2);
    dist = sqrt(dist);

    //scaling from dist to maximum dropOffDist
    //the further away, the darker it gets
    FragColor = v_color * texture2D (u_texture, v_texCoords) * (dropoffDist / max(1,dist));
}
