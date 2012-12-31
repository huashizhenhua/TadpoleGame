package tadpole2d.game;

import system.LAGraphicsUtils;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//游戏View
public class LAGameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "LAGameView";

    private static final long MAX_INTERVAL = 1000L;//最大间隔

    private static final int fpsX = 5;

    private static final int fpsY = 20;

    private transient int width, height;

    private transient boolean start, running;

    private transient long maxFrames, curTime, startTime, offsetTime, curFPS, calcInterval;

    private transient double frameCount;

    private LAHandler handler;

    private SurfaceHolder surfaceHolder;

    private CanvasThread canvasThread;

    private LAGraphics canvasGraphics;

    private LAImage screenImage;

    private Rect rect;

    private LAScreen curScreen;

    /**
     * 是否显示fps帧率
     */
    private transient boolean showFps = false;


    /**
     * 判断Surface是否正在活动中
     */
    private transient boolean isSurfaceActive = false;

    public LAGameView(Activity activity) {
        this(activity, false);
    }

    public LAGameView(Activity activity, boolean isLandScape) {
        super(activity.getApplicationContext());
        LASystem.gc();
        LASystem.setSystemHandler(activity, this);
        this.handler = LASystem.getSystemHandler();
        //		this.handler.setFullScreen();
        this.handler.setLandScape(isLandScape);
        this.setOnCreateContextMenuListener(handler);
        this.setOnFocusChangeListener(handler);
        this.setOnLongClickListener(handler);
        this.setOnKeyListener(handler);
        this.setOnClickListener(handler);
        this.setOnTouchListener(handler);
        this.screenImage = new LAImage(width = handler.getWidth(), height = handler.getHeight());
        this.rect = new Rect(0, 0, width, height);
        System.out.println("width=" + width + ",height=" + height);
        this.canvasThread = new CanvasThread();
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
        this.surfaceHolder.setSizeFromLayout();
        this.setRunning(true);
        this.setFPS(LASystem.DEFAULT_MAX_FPS);
        this.canvasGraphics = screenImage.getLAGraphics();
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.requestFocus();
    }

    class CanvasThread extends Thread {
        public void run() {
            final LTimerContext timerContext = new LTimerContext();
            timerContext.setMillisTime(startTime = System.currentTimeMillis());
            ILAScreen screen = null;
            Canvas canvas = null;
            do {
                if (!start) {
                    continue;
                }
                screen = handler.getScreen();
                canvasGraphics.drawClear();
                screen.createUI(canvasGraphics);
                curTime = System.currentTimeMillis();

                // 设置上次更新所花时间
                timerContext.setSinceLastUpdateTime(curTime - timerContext.getMillisTime());

                // 设置睡眠时间
                timerContext.setMillisSleepTime((offsetTime - timerContext.getSinceLastUpdateTime()) - timerContext.getMillisOverSleepTime());
                if (timerContext.getMillisSleepTime() > 0)//���ÿ֡�ļ��̫�̣�������ʱ��
                {
                    try {
                        Thread.sleep(timerContext.getMillisSleepTime());
                    } catch (InterruptedException e) {
                    }
                    timerContext.setMillisOverSleepTime((System.currentTimeMillis()) - curTime);
                } else {
                    timerContext.setMillisOverSleepTime(0L);
                }
                timerContext.setMillisTime(System.currentTimeMillis());
                screen.runTimer(timerContext);
                if (showFps) {
                    tickFrames();
                    canvasGraphics.setColor(Color.WHITE);
                    canvasGraphics.setAntiAlias(true);
                    canvasGraphics.drawString(("FPS:" + curFPS).intern(), fpsX, fpsY);
                    canvasGraphics.setAntiAlias(false);
                }
                canvas = surfaceHolder.lockCanvas(rect);
                canvas.drawBitmap(screenImage.getBitmap(), 0, 0, null);
                surfaceHolder.unlockCanvasAndPost(canvas);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                }
                LASystem.gc(10000, 1);
            } while (running);
            destroyView();
        }


        private void tickFrames() {
            frameCount++;
            // offsetTime 是理想状态下每刷新一帧所花的时间
            calcInterval += offsetTime;
            if (calcInterval >= MAX_INTERVAL) {
                long timeNow = System.currentTimeMillis();
                long realElapsedTime = timeNow - startTime; // 获取间隔实际时间
                // 计算FPS.(注：转毫秒为秒)
                curFPS = (long) ((frameCount / realElapsedTime) * MAX_INTERVAL);
                // 重置数值，以计算下一次FPS
                frameCount = 0L;
                calcInterval = 0L;
                startTime = timeNow;
            }
        }


    }

    public void destroyView() {
        if (canvasThread != null) {
            canvasThread = null;
        }
        LAGraphicsUtils.destroyImages();
        LASystem.gc();
    }

    public void mainLoop() {
        this.handler.getActivity().setContentView(this);
        this.startPaint();
    }


    public void mianStop() {
        this.endPaint();
    }

    public void startPaint() {
        this.start = true;
    }

    public void endPaint() {
        this.start = false;
    }

    public Thread getCanvasThread() {
        return canvasThread;
    }

    public void setScreen(ILAScreen screen) {
        this.handler.setScreen(screen);
    }

    public boolean getRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        GLog.d(TAG, "----------%s(%b)----------", "setRunning", running);
        this.running = running;
        if (this.running == true && isSurfaceActive == true) {
            resumeCanvasThread();
        }
    }

    public void setFPS(long frames) {
        this.maxFrames = frames;
        this.offsetTime = (long) (1.0 / maxFrames * MAX_INTERVAL);
    }

    public long getMaxFPS() {
        return this.maxFrames;
    }

    public void setShowFPS(boolean isFPS) {
        this.showFps = isFPS;
    }

    public LAHandler getLHandler() {
        return handler;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        GLog.d(TAG, "----------surfaceChanged----------");
        GLog.d(TAG, "surfaceDestroyed (width:%d, height:%d)", this.getWidth(), this.getHeight());
        holder.setFixedSize(width, height);
    }

    public void resumeCanvasThread() {
        if (canvasThread == null) {
            canvasThread = new CanvasThread();
        }
        if (!canvasThread.isAlive()) {
            canvasThread.start();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        GLog.d(TAG, "----------surfaceCreated----------");
        GLog.d(TAG, "surfaceCreated (width:%d, height:%d)", this.getWidth(), this.getHeight());
        isSurfaceActive = true;
        LAScreen laScreen = (LAScreen) handler.getScreen();
        // if screen change . Fire Screen beforeDraw
        if ((curScreen == null) || (!curScreen.equal(laScreen))) {
            laScreen.onCreate(this);
        }
        curScreen = laScreen;
        resumeCanvasThread();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceActive = false;
        GLog.d(TAG, "----------surfaceDestroyed----------");
        GLog.d(TAG, "surfaceDestroyed (width:%d, height:%d)", this.getWidth(), this.getHeight());
        boolean result = true;
        setRunning(false);
        while (result) {
            try {
                //canvasThread.join();
                canvasThread = null;
                result = false;
            } catch (Exception e) {
            }
        }
    }
}