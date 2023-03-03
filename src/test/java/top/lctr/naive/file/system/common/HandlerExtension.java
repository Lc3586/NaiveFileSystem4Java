package top.lctr.naive.file.system.common;

import org.junit.jupiter.api.Assertions;
import project.extension.func.IFunc0;
import project.extension.task.TaskQueueHandlerState;

/**
 * 处理类扩展方法
 *
 * @author LCTR
 * @date 2023-02-01
 */
public class HandlerExtension {
    /**
     * 等待
     *
     * @param explain    说明
     * @param isContinue 是否继续
     */
    public static void wait(String explain,
                            IFunc0<Boolean> isContinue)
            throws
            Throwable {
        wait(explain,
             isContinue,
             60 * 1000);
    }

    /**
     * 等待
     *
     * @param explain    说明
     * @param isContinue 是否继续
     * @param timeout    超时时间
     */
    public static void wait(String explain,
                            IFunc0<Boolean> isContinue,
                            int timeout)
            throws
            Throwable {
        int waitingFor = timeout;
        while (waitingFor > 0) {
            if (!isContinue.invoke())
                return;

            Thread.sleep(300);
            waitingFor -= 300;
        }

        throw new Exception(String.format("%s超时",
                                          explain));
    }

    /**
     * 等待启动
     * <p>超时时间默认为60秒</p>
     *
     * @param name     模块名称
     * @param getState 获取状态
     */
    public static void wait2Start(String name,
                                  IFunc0<TaskQueueHandlerState> getState)
            throws
            Throwable {
        wait2Start(name,
                   getState,
                   60 * 1000);
    }

    /**
     * 等待启动
     *
     * @param name     模块名称
     * @param getState 获取状态
     * @param timeout  超时时间(ms)
     */
    public static void wait2Start(String name,
                                  IFunc0<TaskQueueHandlerState> getState,
                                  int timeout)
            throws
            Throwable {
        int waitingFor = timeout;
        TaskQueueHandlerState lastState = null;
        while (waitingFor > 0) {
            TaskQueueHandlerState state = getState.invoke();

            if (!state.equals(lastState))
                System.out.printf("\r\n%s当前状态：%s\r\n",
                                  name,
                                  state);

            lastState = state;

            if (state.equals(TaskQueueHandlerState.STARTING)) {
                Thread.sleep(300);
                waitingFor -= 300;
                continue;
            }

            Assertions.assertNotEquals(TaskQueueHandlerState.STOPPED,
                                       state,
                                       String.format("%s未启动",
                                                     name));

            return;
        }

        throw new Exception(String.format("等待%s启动超时",
                                          name));
    }

    /**
     * 等待任务执行结束
     * <p>超时时间默认为60秒</p>
     *
     * @param name                   模块名称
     * @param getState               获取状态
     * @param getConcurrentTaskCount 获取子任务数量
     * @param getScheduleTaskCount   获取延时任务数量
     */
    public static void wait2Idle(String name,
                                 IFunc0<TaskQueueHandlerState> getState,
                                 IFunc0<Integer> getConcurrentTaskCount,
                                 IFunc0<Integer> getScheduleTaskCount)
            throws
            Throwable {
        wait2Idle(name,
                  getState,
                  getConcurrentTaskCount,
                  getScheduleTaskCount,
                  60 * 1000);
    }

    /**
     * 等待任务执行结束
     *
     * @param name                   模块名称
     * @param getState               获取状态
     * @param getConcurrentTaskCount 获取子任务数量
     * @param getScheduleTaskCount   获取延时任务数量
     * @param timeout                超时时间(ms)
     */
    public static void wait2Idle(String name,
                                 IFunc0<TaskQueueHandlerState> getState,
                                 IFunc0<Integer> getConcurrentTaskCount,
                                 IFunc0<Integer> getScheduleTaskCount,
                                 int timeout)
            throws
            Throwable {
        int waitingFor = timeout;
        TaskQueueHandlerState lastState = null;
        int lastConcurrentTaskCount = -1;
        int lastScheduleTaskCount = -1;
        while (waitingFor > 0) {
            TaskQueueHandlerState state = getState.invoke();

            if (!state.equals(lastState))
                System.out.printf("\r\n%s当前状态：%s\r\n",
                                  name,
                                  state);

            lastState = state;

            if (state.equals(TaskQueueHandlerState.RUNNING)) {
                Thread.sleep(300);
                waitingFor -= 300;
                continue;
            }

            int concurrentTaskCount = getConcurrentTaskCount.invoke();

            if (concurrentTaskCount != lastConcurrentTaskCount)
                System.out.printf("\r\n%s当前子任务数量：%s\r\n",
                                  name,
                                  concurrentTaskCount);

            lastConcurrentTaskCount = concurrentTaskCount;

            if (concurrentTaskCount > 0) {
                Thread.sleep(300);
                waitingFor -= 300;
                continue;
            }

            int scheduleTaskCount = getScheduleTaskCount.invoke();

            if (scheduleTaskCount != lastScheduleTaskCount)
                System.out.printf("\r\n%s当前延时任务数量：%s\r\n",
                                  name,
                                  scheduleTaskCount);

            lastScheduleTaskCount = scheduleTaskCount;

            if (scheduleTaskCount > 0) {
                Thread.sleep(300);
                waitingFor -= 300;
                continue;
            }

            return;
        }

        throw new Exception(String.format("等待%s任务执行结束超时",
                                          name));
    }
}
