package com.starrydeveloper.mtyper;

import android.content.*;
import android.graphics.*;
import android.net.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import java.util.*;

/**
 * Recognition Progress View
 *
 * @author Vikram Ezhil
 */

class RecognitionProgressView extends View
{
    public static final int BARS_COUNT = 5;

    private static final int CIRCLE_RADIUS_DP = 7;
    private static final int CIRCLE_SPACING_DP = 1;
    private static final int ROTATION_RADIUS_DP = 25;
    private static final int IDLE_FLOATING_AMPLITUDE_DP = 10;

    private static final int[] DEFAULT_BARS_HEIGHT_DP = { 60, 46, 70, 54, 64 };

    private static final float MDPI_DENSITY = 1.5f;

    private final List<RecognitionBarView> recognitionBars = new ArrayList<>();
    private Paint paint;
    private OnBarParamsAnimListener animator;

    private int radius;
    private int spacing;
    private int rotationRadius;
    private int amplitude;

    private float density;

    private boolean animating;

    private int barColor = -1;
    private int[] barColors;
    private int[] barMaxHeights;

    // MARK: SpeechProgressView Constructors

    public RecognitionProgressView(Context context)
    {
        super(context);

        init();
    }

    public RecognitionProgressView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init();
    }

    public RecognitionProgressView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        init();
    }

    // MARK: View Methods

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        if(recognitionBars.isEmpty())
        {
            initBars();
        }
        else if(changed)
        {
            recognitionBars.clear();

            initBars();
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(recognitionBars.isEmpty())
        {
            return;
        }

        if(animating)
        {
            animator.animate();
        }

        for(int i = 0; i < recognitionBars.size(); i++)
        {
            RecognitionBarView bar = recognitionBars.get(i);

            if(barColors != null)
            {
                paint.setColor(barColors[i]);
            }
            else if (barColor != -1)
            {
                paint.setColor(barColor);
            }

            canvas.drawRoundRect(bar.getRect(), radius, radius, paint);
        }

        if(animating)
        {
            invalidate();
        }
    }

    /**
     * Starts animating view
     */
    public void play() {
        startIdleInterpolation();
        animating = true;
    }

    /**
     * Stops animating view
     */
    public void stop() {
        if (animator != null) {
            animator.stop();
            animator = null;
        }
        animating = false;
        resetBars();
    }

    /**
     * Set one color to all bars in view
     */
    public void setSingleColor(int color) {
        barColor = color;
    }

    /**
     * Set different colors to bars in view
     *
     * @param colors - array with size = {@link #BARS_COUNT}
     */
    public void setColors(int[] colors) {
        if (colors == null) return;

        barColors = new int[BARS_COUNT];
        if (colors.length < BARS_COUNT) {
            System.arraycopy(colors, 0, barColors, 0, colors.length);
            for (int i = colors.length; i < BARS_COUNT; i++) {
                barColors[i] = colors[0];
            }
        } else {
            System.arraycopy(colors, 0, barColors, 0, BARS_COUNT);
        }
    }

    /**
     * Set sizes of bars in view
     *
     * @param heights - array with size = {@link #BARS_COUNT},
     * if not set uses default bars heights
     */
    public void setBarMaxHeightsInDp(int[] heights) {
        if (heights == null) return;

        barMaxHeights = new int[BARS_COUNT];
        if (heights.length < BARS_COUNT) {
            System.arraycopy(heights, 0, barMaxHeights, 0, heights.length);
            for (int i = heights.length; i < BARS_COUNT; i++) {
                barMaxHeights[i] = heights[0];
            }
        } else {
            System.arraycopy(heights, 0, barMaxHeights, 0, BARS_COUNT);
        }
    }

    /**
     * Set radius of circle
     *
     * @param radius - Default value = {@link #CIRCLE_RADIUS_DP}
     */
    public void setCircleRadiusInDp(int radius) {
        this.radius = (int) (radius * density);
    }

    /**
     * Set spacing between circles
     *
     * @param spacing - Default value = {@link #CIRCLE_SPACING_DP}
     */
    public void setSpacingInDp(int spacing) {
        this.spacing = (int) (spacing * density);
    }

    /**
     * Set idle animation amplitude
     *
     * @param amplitude - Default value = {@link #IDLE_FLOATING_AMPLITUDE_DP}
     */
    public void setIdleStateAmplitudeInDp(int amplitude) {
        this.amplitude = (int) (amplitude * density);
    }

    /**
     * Set rotation animation radius
     *
     * @param radius - Default value = {@link #ROTATION_RADIUS_DP}
     */
    public void setRotationRadiusInDp(int radius) {
        this.rotationRadius = (int) (radius * density);
    }

    private void init()
    {
        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GRAY);

        density = getResources().getDisplayMetrics().density;

        radius = (int) (CIRCLE_RADIUS_DP * density);
        spacing = (int) (CIRCLE_SPACING_DP * density);
        rotationRadius = (int) (ROTATION_RADIUS_DP * density);
        amplitude = (int) (IDLE_FLOATING_AMPLITUDE_DP * density);

        if (density <= MDPI_DENSITY) {
            amplitude *= 2;
        }
    }

    private void initBars()
    {
        final List<Integer> heights = initBarHeights();

        int firstCirclePosition = getMeasuredWidth() / 2 -
			2 * spacing -
			4 * radius;

        for (int i = 0; i < BARS_COUNT; i++)
        {
            int x = firstCirclePosition + (2 * radius + spacing) * i;

            RecognitionBarView bar = new RecognitionBarView(x, getMeasuredHeight() / 2, 2 * radius, heights.get(i), radius);

            recognitionBars.add(bar);
        }
    }

    private List<Integer> initBarHeights()
    {
        final List<Integer> barHeights = new ArrayList<>();

        if(barMaxHeights == null)
        {
            for(int i = 0; i < BARS_COUNT; i++)
            {
                barHeights.add((int) (DEFAULT_BARS_HEIGHT_DP[i] * density));
            }
        }
        else
        {
            for(int i = 0; i < BARS_COUNT; i++)
            {
                barHeights.add((int) (barMaxHeights[i] * density));
            }
        }

        return barHeights;
    }

    private void resetBars()
    {
        for (RecognitionBarView bar : recognitionBars)
        {
            bar.setX(bar.getStartX());
            bar.setY(bar.getStartY());
            bar.setHeight(radius * 2);
            bar.update();
        }
    }

    private void startIdleInterpolation()
    {
        animator = new AnimatorIdle(recognitionBars, amplitude);
        animator.start();
    }

    private void startRmsInterpolation()
    {
        resetBars();

        animator = new AnimatorRms(recognitionBars);
        animator.start();
    }

    private void startTransformInterpolation()
    {
        resetBars();

        animator = new AnimatorTransform(recognitionBars, getWidth() / 2, getHeight() / 2, rotationRadius);

        animator.start();

        ((AnimatorTransform) animator).setOnInterpolationFinishedListener(new AnimatorTransform.OnInterpolationFinishedListener()
			{
				@Override
				public void onFinished() {
					startRotateInterpolation();
				}
			});
    }

    private void startRotateInterpolation()
    {
        animator = new AnimatorRotating(recognitionBars, getWidth() / 2, getHeight() / 2);

        animator.start();
    }

    /**
     * Updates the view with the rmsDB Value
     *
     * @param rmsdB The rmsdb value
     */
    public void rmsValue(float rmsdB)
    {
        if (animator == null || rmsdB < 1f)
        {
            return;
        }

        if (!(animator instanceof AnimatorRms))
        {
            startRmsInterpolation();
        }

        if (animator instanceof AnimatorRms)
        {
            ((AnimatorRms) animator).onRmsChanged(rmsdB);
        }
    }
} 
//////////
class Extensions
{
    final static int[] PV_COLORS = new int[] {Color.BLUE, Color.RED, Color.argb(255,255,165,0), Color.argb(255,178,34,34), Color.argb(255,34,139,34)};
    final static int[] PV_BARS_HEIGHT = new int[] {24, 28, 22, 27, 20};
    final static int PV_HEIGHT = 100;
    final static int PV_CIRCLE_RADIUS = 7;
    final static int PV_CIRCLE_SPACING = 2;
    final static int PV_IDLE_STATE = 2;
    final static int PV_ROTATION_RADIUS = 10;
    final static int MAX_VOICE_RESULTS = 5;
    final static int MAX_PAUSE_TIME = 500;
    final static int PARTIAL_DELAY_TIME = 500;
    final static int ERROR_TIMEOUT = 5000;
    final static int AUDIO_BEEP_DISABLED_TIMEOUT = 30000;

    /**
     * Checks if the internet is enabled
     *
     * @param context The application context instance
     *
     * @return The internet enabled status
     */
    static boolean isInternetEnabled(Context context)
    {
        // Initializing the connectivity Manager
        ConnectivityManager activeConnection = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Getting the network information
        NetworkInfo networkInfo = activeConnection.getActiveNetworkInfo();

        return networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }
}
//////////
class AnimatorTransform implements OnBarParamsAnimListener
{
    private static final long DURATION = 300;

    private long startTimestamp;
    private boolean isPlaying;

    private OnInterpolationFinishedListener listener;

    private final int radius;
    private final int centerX, centerY;
    private final List<Point> finalPositions = new ArrayList<>();
    private final List<RecognitionBarView> bars;

    AnimatorTransform(List<RecognitionBarView> bars, int centerX, int centerY, int radius)
    {
        this.centerX = centerX;
        this.centerY = centerY;
        this.bars = bars;
        this.radius = radius;
    }

    @Override
    public void start()
    {
        isPlaying = true;
        startTimestamp = System.currentTimeMillis();
        initFinalPositions();
    }

    @Override
    public void stop()
    {
        isPlaying = false;
        if (listener != null)
        {
            listener.onFinished();
        }
    }

    @Override
    public void animate()
    {
        if (!isPlaying) return;

        long currTimestamp = System.currentTimeMillis();
        long delta = currTimestamp - startTimestamp;
        if (delta > DURATION)
        {
            delta = DURATION;
        }

        for (int i = 0; i < bars.size(); i++)
        {
            RecognitionBarView bar = bars.get(i);

            int x = bar.getStartX() + (int) ((finalPositions.get(i).x - bar.getStartX()) * ((float) delta / DURATION));
            int y = bar.getStartY() + (int) ((finalPositions.get(i).y - bar.getStartY()) * ((float) delta / DURATION));

            bar.setX(x);
            bar.setY(y);
            bar.update();
        }


        if (delta == DURATION)
        {
            stop();
        }
    }

    private void initFinalPositions()
    {
        Point startPoint = new Point();
        startPoint.x = centerX;
        startPoint.y = centerY - radius;
        for (int i = 0; i < RecognitionProgressView.BARS_COUNT; i++)
        {
            Point point = new Point(startPoint);
            rotate((360d / RecognitionProgressView.BARS_COUNT) * i, point);
            finalPositions.add(point);
        }
    }

    /**
     * X = x0 + (x - x0) * cos(a) - (y - y0) * sin(a);
     * Y = y0 + (y - y0) * cos(a) + (x - x0) * sin(a);
     **/
    private void rotate(double degrees, Point point) {

        double angle = Math.toRadians(degrees);

        int x = centerX + (int) ((point.x - centerX) * Math.cos(angle) -
			(point.y - centerY) * Math.sin(angle));

        int y = centerY + (int) ((point.x - centerX) * Math.sin(angle) +
			(point.y - centerY) * Math.cos(angle));

        point.x = x;
        point.y = y;
    }

    void setOnInterpolationFinishedListener(OnInterpolationFinishedListener listener)
    {
        this.listener = listener;
    }

    interface OnInterpolationFinishedListener
    {
        void onFinished();
    }
}
//////////

class RecognitionBarView
{
    private int x;
    private int y;
    private int radius;
    private int height;

    private final int maxHeight;
    private final int startX;
    private final int startY;
    final private RectF rect;

    RecognitionBarView(int x, int y, int height, int maxHeight, int radius)
    {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.startX = x;
        this.startY = y;
        this.height = height;
        this.maxHeight = maxHeight;
        this.rect = new RectF(x - radius,
							  y - height / 2,
							  x + radius,
							  y + height / 2);
    }

    void update()
    {
        rect.set(x - radius,
				 y - height / 2,
				 x + radius,
				 y + height / 2);
    }

    int getX()
    {
        return x;
    }

    void setX(int x)
    {
        this.x = x;
    }

    int getY()
    {
        return y;
    }

    void setY(int y)
    {
        this.y = y;
    }

    int getHeight()
    {
        return height;
    }

    void setHeight(int height)
    {
        this.height = height;
    }

    int getMaxHeight()
    {
        return maxHeight;
    }

    int getStartX()
    {
        return startX;
    }

    int getStartY()
    {
        return startY;
    }

    RectF getRect()
    {
        return rect;
    }

    int getRadius()
    {
        return radius;
    }
}
//////////

interface OnBarParamsAnimListener
{
    /**
     * Sends update to start animation
     */
    void start();

    /**
     * Sends update to stop animation
     */
    void stop();

    /**
     * Sends update to animate
     */
    void animate();
}
//////////
class AnimatorRotating implements OnBarParamsAnimListener
{
    private static final long DURATION = 2000;
    private static final long ACCELERATE_ROTATION_DURATION = 1000;
    private static final long DECELERATE_ROTATION_DURATION = 1000;
    private static final float ROTATION_DEGREES = 720f;
    private static final float ACCELERATION_ROTATION_DEGREES = 40f;

    private long startTimestamp;
    private boolean isPlaying;

    private final int centerX, centerY;
    private final List<Point> startPositions;
    private final List<RecognitionBarView> bars;

    AnimatorRotating(List<RecognitionBarView> bars, int centerX, int centerY)
    {
        this.centerX = centerX;
        this.centerY = centerY;
        this.bars = bars;
        this.startPositions = new ArrayList<>();
        for (RecognitionBarView bar : bars)
        {
            startPositions.add(new Point(bar.getX(), bar.getY()));
        }
    }

    @Override
    public void start()
    {
        isPlaying = true;
        startTimestamp = System.currentTimeMillis();
    }

    @Override
    public void stop()
    {
        isPlaying = false;
    }

    @Override
    public void animate()
    {
        if (!isPlaying) return;

        long currTimestamp = System.currentTimeMillis();
        if (currTimestamp - startTimestamp > DURATION) {
            startTimestamp += DURATION;
        }

        long delta = currTimestamp - startTimestamp;

        float interpolatedTime = (float) delta / DURATION;

        float angle = interpolatedTime * ROTATION_DEGREES;

        int i = 0;
        for (RecognitionBarView bar : bars)
        {
            float finalAngle = angle;
            if (i > 0 && delta > ACCELERATE_ROTATION_DURATION)
            {
                finalAngle += decelerate(delta, bars.size() - i);
            }
            else if (i > 0)
            {
                finalAngle += accelerate(delta, bars.size() - i);
            }
            rotate(bar, finalAngle, startPositions.get(i));
            i++;
        }
    }

    private float decelerate(long delta, int scale)
    {
        long accelerationDelta = delta - ACCELERATE_ROTATION_DURATION;
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        float interpolatedTime = interpolator.getInterpolation((float) accelerationDelta / DECELERATE_ROTATION_DURATION);
        float decelerationAngle = -interpolatedTime * (ACCELERATION_ROTATION_DEGREES * scale);
        return ACCELERATION_ROTATION_DEGREES * scale + decelerationAngle;
    }

    private float accelerate(long delta, int scale)
    {
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        float interpolatedTime = interpolator.getInterpolation((float) delta / ACCELERATE_ROTATION_DURATION);

        return interpolatedTime * (ACCELERATION_ROTATION_DEGREES * scale);
    }

    /**
     * X = x0 + (x - x0) * cos(a) - (y - y0) * sin(a);
     * Y = y0 + (y - y0) * cos(a) + (x - x0) * sin(a);
     */
    private void rotate(RecognitionBarView bar, double degrees, Point startPosition)
    {
        double angle = Math.toRadians(degrees);

        int x = centerX + (int) ((startPosition.x - centerX) * Math.cos(angle) -
			(startPosition.y - centerY) * Math.sin(angle));

        int y = centerY + (int) ((startPosition.x - centerX) * Math.sin(angle) +
			(startPosition.y - centerY) * Math.cos(angle));

        bar.setX(x);
        bar.setY(y);
        bar.update();
    }
}
//////////
class AnimatorRms implements OnBarParamsAnimListener
{
    final private List<AnimatorBarRms> barAnimators;

    AnimatorRms(List<RecognitionBarView> recognitionBars)
    {
        this.barAnimators = new ArrayList<>();

        for(RecognitionBarView bar : recognitionBars)
        {
            barAnimators.add(new AnimatorBarRms(bar));
        }
    }

    @Override
    public void start()
    {
        for(AnimatorBarRms barAnimator : barAnimators)
        {
            barAnimator.start();
        }
    }

    @Override
    public void stop()
    {
        for(AnimatorBarRms barAnimator : barAnimators)
        {
            barAnimator.stop();
        }
    }

    @Override
    public void animate()
    {
        for(AnimatorBarRms barAnimator : barAnimators)
        {
            barAnimator.animate();
        }
    }

    public void onRmsChanged(float rmsDB)
    {
        for (AnimatorBarRms barAnimator : barAnimators)
        {
            barAnimator.onRmsChanged(rmsDB);
        }
    }
}
//////////
//////////
class AnimatorBarRms implements OnBarParamsAnimListener
{
    private static final float QUIT_RMSDB_MAX = 2f;
    private static final float MEDIUM_RMSDB_MAX = 5.5f;

    private static final long BAR_ANIMATION_UP_DURATION = 130;
    private static final long BAR_ANIMATION_DOWN_DURATION = 500;
    final private RecognitionBarView bar;
    private float fromHeightPart;
    private float toHeightPart;
    private long startTimestamp;
    private boolean isPlaying;
    private boolean isUpAnimation;

    AnimatorBarRms(RecognitionBarView bar)
    {
        this.bar = bar;
    }

    @Override
    public void start()
    {
        isPlaying = true;
    }

    @Override
    public void stop()
    {
        isPlaying = false;
    }

    @Override
    public void animate()
    {
        if (isPlaying)
        {
            update();
        }
    }

    void onRmsChanged(float rmsdB)
    {
        float newHeightPart;

        if (rmsdB < QUIT_RMSDB_MAX) {
            newHeightPart = 0.2f;
        } else if (rmsdB >= QUIT_RMSDB_MAX && rmsdB <= MEDIUM_RMSDB_MAX) {
            newHeightPart = 0.3f + new Random().nextFloat();
            if (newHeightPart > 0.6f) newHeightPart = 0.6f;
        } else {
            newHeightPart = 0.7f + new Random().nextFloat();
            if (newHeightPart > 1f) newHeightPart = 1f;

        }

        if (newHeightIsSmallerCurrent(newHeightPart)) {
            return;
        }

        fromHeightPart = (float) bar.getHeight() / bar.getMaxHeight();
        toHeightPart = newHeightPart;

        startTimestamp = System.currentTimeMillis();
        isUpAnimation = true;
        isPlaying = true;
    }

    private boolean newHeightIsSmallerCurrent(float newHeightPart)
    {
        return (float) bar.getHeight() / bar.getMaxHeight() > newHeightPart;
    }

    private void update()
    {
        long currTimestamp = System.currentTimeMillis();
        long delta = currTimestamp - startTimestamp;

        if (isUpAnimation) {
            animateUp(delta);
        } else {
            animateDown(delta);
        }
    }

    private void animateUp(long delta)
    {
        boolean finished = false;
        int minHeight = (int) (fromHeightPart * bar.getMaxHeight());
        int toHeight = (int) (bar.getMaxHeight() * toHeightPart);

        float timePart = (float) delta / BAR_ANIMATION_UP_DURATION;

        AccelerateInterpolator interpolator = new AccelerateInterpolator();
        int height = minHeight + (int) (interpolator.getInterpolation(timePart) * (toHeight - minHeight));

        if (height < bar.getHeight()) {
            return;
        }

        if (height >= toHeight) {
            height = toHeight;
            finished = true;
        }

        bar.setHeight(height);
        bar.update();

        if (finished) {
            isUpAnimation = false;
            startTimestamp = System.currentTimeMillis();
        }
    }

    private void animateDown(long delta)
    {
        int minHeight = bar.getRadius() * 2;
        int fromHeight = (int) (bar.getMaxHeight() * toHeightPart);

        float timePart = (float) delta / BAR_ANIMATION_DOWN_DURATION;

        DecelerateInterpolator interpolator = new DecelerateInterpolator();
        int height = minHeight + (int) ((1f - interpolator.getInterpolation(timePart)) * (fromHeight - minHeight));

        if (height > bar.getHeight()) {
            return;
        }

        if (height <= minHeight) {
            finish();
            return;
        }

        bar.setHeight(height);
        bar.update();
    }

    private void finish()
    {
        bar.setHeight(bar.getRadius() * 2);
        bar.update();
        isPlaying = false;
    }
}
//////////

//////////
class AnimatorIdle implements OnBarParamsAnimListener
{
    private static final long IDLE_DURATION = 1500;

    private long startTimestamp;
    private boolean isPlaying;

    private final int floatingAmplitude;
    private final List<RecognitionBarView> bars;

    AnimatorIdle(List<RecognitionBarView> bars, int floatingAmplitude)
    {
        this.floatingAmplitude = floatingAmplitude;
        this.bars = bars;
    }

    @Override
    public void start()
    {
        isPlaying = true;
        startTimestamp = System.currentTimeMillis();
    }

    @Override
    public void stop()
    {
        isPlaying = false;
    }

    @Override
    public void animate()
    {
        if (isPlaying)
        {
            update(bars);
        }
    }

    void update(List<RecognitionBarView> bars)
    {

        long currTimestamp = System.currentTimeMillis();
        if (currTimestamp - startTimestamp > IDLE_DURATION)
        {
            startTimestamp += IDLE_DURATION;
        }

        long delta = currTimestamp - startTimestamp;
        int i = 0;
        for (RecognitionBarView bar : bars)
        {
            updateCirclePosition(bar, delta, i);
            i++;
        }
    }

    private void updateCirclePosition(RecognitionBarView bar, long delta, int num)
    {
        float angle = ((float) delta / IDLE_DURATION) * 360f + 120f * num;
        int y = (int) (Math.sin(Math.toRadians(angle)) * floatingAmplitude) + bar.getStartY();
        bar.setY(y);
        bar.update();
    }
}
//////////
