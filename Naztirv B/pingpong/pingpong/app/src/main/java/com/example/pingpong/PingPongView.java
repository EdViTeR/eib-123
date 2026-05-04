package com.example.pingpong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PingPongView extends View {

    private static final int FIELD_WIDTH = 400;
    private static final int FIELD_HEIGHT = 400;
    private static final int BALL_RADIUS = 20;
    private static final int PADDLE_WIDTH = 20;
    private static final int PADDLE_HEIGHT = 100;
    private static final float BALL_SPEED = 5f;

    private Paint fieldPaint;
    private Paint ballPaint;
    private Paint paddlePaint;
    private Paint netPaint;

    private Ball ball;

    private float leftPaddleY = FIELD_HEIGHT / 2f - PADDLE_HEIGHT / 2f;
    private float rightPaddleY = FIELD_HEIGHT / 2f - PADDLE_HEIGHT / 2f;

    private float leftPaddleDirection = 1f;
    private long lastTime = System.currentTimeMillis();

    public PingPongView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        fieldPaint = new Paint();
        fieldPaint.setColor(Color.parseColor("#2E7D32"));
        fieldPaint.setStyle(Paint.Style.FILL);

        ballPaint = new Paint();
        ballPaint.setColor(Color.WHITE);

        paddlePaint = new Paint();
        paddlePaint.setColor(Color.parseColor("#8B4513"));

        netPaint = new Paint();
        netPaint.setColor(Color.WHITE);
        netPaint.setStrokeWidth(2);
        netPaint.setStyle(Paint.Style.STROKE);

        ball = new Ball(FIELD_WIDTH / 2f, FIELD_HEIGHT / 2f, BALL_RADIUS, BALL_SPEED);
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastTime) / 1000f;
        lastTime = currentTime;

        moveLeftPaddle(deltaTime);
        ball.update();
        checkCollisions();

        invalidate();
    }

    private void moveLeftPaddle(float deltaTime) {
        float speed = FIELD_HEIGHT / 2f;
        leftPaddleY += leftPaddleDirection * speed * deltaTime;

        if (leftPaddleY < 0) {
            leftPaddleY = 0;
            leftPaddleDirection = 1f;
        } else if (leftPaddleY > FIELD_HEIGHT - PADDLE_HEIGHT) {
            leftPaddleY = FIELD_HEIGHT - PADDLE_HEIGHT;
            leftPaddleDirection = -1f;
        }
    }

    private void checkCollisions() {
        if (ball.y - ball.radius < 0) {
            ball.y = ball.radius;
            ball.vy = -ball.vy;
        } else if (ball.y + ball.radius > FIELD_HEIGHT) {
            ball.y = FIELD_HEIGHT - ball.radius;
            ball.vy = -ball.vy;
        }

        if (ball.x - ball.radius < PADDLE_WIDTH &&
                ball.y + ball.radius > leftPaddleY &&
                ball.y - ball.radius < leftPaddleY + PADDLE_HEIGHT) {

            ball.x = PADDLE_WIDTH + ball.radius;
            calculatePaddleBounce(leftPaddleY, true);
        }

        if (ball.x + ball.radius > FIELD_WIDTH - PADDLE_WIDTH &&
                ball.y + ball.radius > rightPaddleY &&
                ball.y - ball.radius < rightPaddleY + PADDLE_HEIGHT) {

            ball.x = FIELD_WIDTH - PADDLE_WIDTH - ball.radius;
            calculatePaddleBounce(rightPaddleY, false);
        }

        if (ball.x - ball.radius < 0 || ball.x + ball.radius > FIELD_WIDTH) {
            ball.reset();
        }
    }

    private void calculatePaddleBounce(float paddleY, boolean isLeft) {
        float relativeIntersectY = (paddleY + PADDLE_HEIGHT / 2) - ball.y;
        float normalizedIntersectY = relativeIntersectY / (PADDLE_HEIGHT / 2);
        normalizedIntersectY = Math.max(-1.0f, Math.min(1.0f, normalizedIntersectY));

        float bounceAngle = normalizedIntersectY * (float) (Math.PI / 4);
        float direction = isLeft ? 1 : -1;

        float newVx = direction * ball.speed * (float) Math.cos(bounceAngle);

        float newVy = ball.speed * (float) -Math.sin(bounceAngle);

        newVy += (float) (Math.random() - 0.5) * 2;

        ball.setVelocity(newVx, newVy);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        float scaleX = (float) getWidth() / FIELD_WIDTH;
        float scaleY = (float) getHeight() / FIELD_HEIGHT;
        canvas.scale(scaleX, scaleY);

        canvas.drawRect(0, 0, FIELD_WIDTH, FIELD_HEIGHT, fieldPaint);
        canvas.drawLine(FIELD_WIDTH / 2f, 0, FIELD_WIDTH / 2f, FIELD_HEIGHT, netPaint);
        canvas.drawCircle(FIELD_WIDTH / 2f, FIELD_HEIGHT / 2f, 50, netPaint);

        canvas.drawRect(0, leftPaddleY, PADDLE_WIDTH, leftPaddleY + PADDLE_HEIGHT, paddlePaint);
        canvas.drawRect(FIELD_WIDTH - PADDLE_WIDTH, rightPaddleY,
                FIELD_WIDTH, rightPaddleY + PADDLE_HEIGHT, paddlePaint);

        canvas.drawCircle(ball.x, ball.y, ball.radius, ballPaint);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE ||
                event.getAction() == MotionEvent.ACTION_DOWN) {

            float touchY = event.getY() * FIELD_HEIGHT / getHeight();
            rightPaddleY = touchY - PADDLE_HEIGHT / 2f;

            if (rightPaddleY < 0) {
                rightPaddleY = 0;
            } else if (rightPaddleY > FIELD_HEIGHT - PADDLE_HEIGHT) {
                rightPaddleY = FIELD_HEIGHT - PADDLE_HEIGHT;
            }

            invalidate();
            return true;
        }

        return super.onTouchEvent(event);
    }
}
