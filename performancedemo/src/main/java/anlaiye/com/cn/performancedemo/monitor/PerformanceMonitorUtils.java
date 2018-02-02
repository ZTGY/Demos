package anlaiye.com.cn.performancedemo.monitor;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;
import android.view.Choreographer;
import android.view.FrameMetrics;
import android.view.Window;

/**
 * Intro:
 * Author: zhangxutong
 * E-mail: mcxtzhang@163.com
 * Home Page: http://blog.csdn.net/zxt0601
 * Created:   2017/10/26.
 * History:
 */

public class PerformanceMonitorUtils {
    private static final String TAG = "zxt/PerformanceMonitorUtils";

    public static void monitorMainLooper() {

        Looper.getMainLooper().setMessageLogging(new Printer() {
            boolean isStarted = false;
            long lastTime;

            @Override
            public void println(String x) {
                //只有looper里有msg 才会执行
                if (isStarted) {
                    isStarted = false;
                    Log.d("TAG", "本次主线程 操作执行时间 : x = [" + (System.currentTimeMillis() - lastTime) + "]");
                } else {
                    isStarted = true;
                    lastTime = System.currentTimeMillis();
                }
            }
        });
    }

    public static void monitorChoreoGrapher() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //一直会执行, 这里的消息其实是插入到主线程的handler里执行的，所以如果这里有任务，那么主线程的Handler也会一直运行。 上个方法monitorMainLooper也会一直运行
            Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
                long lastTime;

                @Override
                public void doFrame(long frameTimeNanos) {
                    long gap = ((frameTimeNanos - lastTime) / 1000000);
                    Log.d(TAG, "doFrame: frameTimeNanos = [" + frameTimeNanos + "]" + "lasttime:" + lastTime + ", gap:" + gap);
                    if (gap > 16) {
                        long count = (gap - 16) / 16;
                        Log.e(TAG, "丢帧 : frameTimeNanos = [" + frameTimeNanos + "]" + "lasttime:" + lastTime + ", gap:" + gap + ",丢了几帧:" + count);

                    }
                    lastTime = frameTimeNanos;
                    Choreographer.getInstance().postFrameCallback(this);
                }
            });
        }
    }


    public static void monitorFrameMetrics(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activity.getWindow().addOnFrameMetricsAvailableListener(new Window.OnFrameMetricsAvailableListener() {
                @Override
                public void onFrameMetricsAvailable(Window window, FrameMetrics frameMetrics, int dropCountSinceLastInvocation) {
                    //Log.d(TAG, "onFrameMetricsAvailable() called with: window = [" + window + "], frameMetrics = [" + frameMetrics + "], dropCountSinceLastInvocation = [" + dropCountSinceLastInvocation + "]");


                    final long UNKNOWN_DELAY_DURATION = frameMetrics.getMetric(FrameMetrics.UNKNOWN_DELAY_DURATION);
                    final long INPUT_HANDLING_DURATION = frameMetrics.getMetric(FrameMetrics.INPUT_HANDLING_DURATION);
                    final long ANIMATION_DURATION = frameMetrics.getMetric(FrameMetrics.ANIMATION_DURATION);
                    final long LAYOUT_MEASURE_DURATION = frameMetrics.getMetric(FrameMetrics.LAYOUT_MEASURE_DURATION);
                    final long DRAW_DURATION = frameMetrics.getMetric(FrameMetrics.DRAW_DURATION);
                    final long SYNC_DURATION = frameMetrics.getMetric(FrameMetrics.SYNC_DURATION);
                    final long COMMAND_ISSUE_DURATION = frameMetrics.getMetric(FrameMetrics.COMMAND_ISSUE_DURATION);
                    final long SWAP_BUFFERS_DURATION = frameMetrics.getMetric(FrameMetrics.SWAP_BUFFERS_DURATION);
                    final long TOTAL_DURATION = frameMetrics.getMetric(FrameMetrics.TOTAL_DURATION);

                    final long FIRST_DRAW_FRAME = frameMetrics.getMetric(FrameMetrics.FIRST_DRAW_FRAME);

                    Log.e(TAG, "onFrameMetricsAvailable："
                            + "UNKNOWN_DELAY_DURATION：[" + UNKNOWN_DELAY_DURATION + "],"
                            + "INPUT_HANDLING_DURATION：[" + INPUT_HANDLING_DURATION + "],"
                            + "ANIMATION_DURATION：[" + ANIMATION_DURATION + "],"
                            + "LAYOUT_MEASURE_DURATION：[" + LAYOUT_MEASURE_DURATION + "],"
                            + "DRAW_DURATION：[" + DRAW_DURATION + "],"
                            + "SYNC_DURATION：[" + SYNC_DURATION + "],"
                            + "COMMAND_ISSUE_DURATION：[" + COMMAND_ISSUE_DURATION + "],"
                            + "SWAP_BUFFERS_DURATION：[" + SWAP_BUFFERS_DURATION + "],"
                            + "TOTAL_DURATION：[" + TOTAL_DURATION + "],"
                            + "FIRST_DRAW_FRAME：[" + FIRST_DRAW_FRAME + "],");

                }
            }, new Handler(Looper.getMainLooper()));
        }
    }
}