package com.example.pingpong;

public class Ball {
    public float x, y;
    public float vx, vy;
    public float radius;
    public float speed;

    public Ball(float x, float y, float radius, float speed) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = speed;
        resetDirection();
    }

    public void resetDirection() {
        double angle = Math.random() * 2 * Math.PI;
        this.vx = (float) (Math.cos(angle) * speed);
        this.vy = (float) (Math.sin(angle) * speed);

        if (Math.abs(vx) < 1.0f) {
            vx = vx > 0 ? 1.0f : -1.0f;
        }
        if (Math.abs(vy) < 1.0f) {
            vy = vy > 0 ? 1.0f : -1.0f;
        }
    }

    public void reset() {
        this.x = 300;
        this.y = 200;
        resetDirection();
    }

    public void update() {
        x += vx;
        y += vy;
    }

    public void setVelocity(float newVx, float newVy) {
        float currentSpeed = (float) Math.sqrt(newVx * newVx + newVy * newVy);
        if (currentSpeed > 0) {
            this.vx = newVx / currentSpeed * speed;
            this.vy = newVy / currentSpeed * speed;
        }
    }
}
