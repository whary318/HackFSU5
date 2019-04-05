import com.leapmotion.leap.*;
import com.leapmotion.leap.Frame;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

class CustomListenerAdjustable extends Listener {
    public Robot robot;
    private Dimension screen;
    private boolean xAxis = true;
    private boolean yAxis = true;
    private int xAxisInt;
    private int yAxisInt;
    //private int[] keyMap = {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D};

    //when adding a controller to this class, this method is executed.
    public void onConnect(Controller c) {
        c.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        c.enableGesture(Gesture.Type.TYPE_CIRCLE);
        c.enableGesture(Gesture.Type.TYPE_KEY_TAP);
//        c.config().set("Gesture.KeyTap.MinDownVelocity", 40.0);
//        c.config().set("Gesture.KeyTap.HistorySeconds", 0.2);
//        c.config().set("Gesture.KeyTap.MinDistance", 1.0);
//        c.config().save();
    }

    //every frame refresh this method is executed.
    public void onFrame(Controller c) {
        try {
            robot = new Robot();
        } catch (Exception e) {
        }
        Frame frame = c.frame();
        InteractionBox box = frame.interactionBox();

        //tracking index finger here
        for (Finger f : frame.fingers()) {
            if (f.type() == Finger.Type.TYPE_INDEX) {
                Vector fingerPos = f.stabilizedTipPosition();
                Vector boxFingerPos = box.normalizePoint(fingerPos);
                robot.mouseMove(robotMouseX(boxFingerPos), robotMouseY(boxFingerPos));
            }
        }

        //tracking type of movement(swiping, tapping, circling).
        for (Gesture g : frame.gestures()) {
            if (g.type() == Gesture.Type.TYPE_KEY_TAP) {
                tappingScreen(robot);
            } else if (g.type() == Gesture.Type.TYPE_CIRCLE) {
//                robot.keyPress(KeyEvent.VK_WINDOWS);
//                robot.keyRelease(KeyEvent.VK_WINDOWS);
//                SwipeGesture swipe = new SwipeGesture(g);
//                handDirectionMappedKeys(swipe.direction());
                CircleGesture circle = new CircleGesture(g);
                if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 4) {
                    //robot.mouseWheel(1);
                    robot.keyPress(KeyEvent.VK_RIGHT);
                    robot.keyRelease(KeyEvent.VK_RIGHT);
//                    try {
//                        Thread.sleep(50);
//                    } catch (Exception e) {
//                    }
                } else {
                    //robot.mouseWheel(-1);
                    robot.keyPress(KeyEvent.VK_LEFT);
                    robot.keyRelease(KeyEvent.VK_LEFT);
//                    try {
//                        Thread.sleep(50);
//                    } catch (Exception e) {
//                    }
                }
            }
        }
    }

    // calculating the mouse position (x and y separated).
    private int robotMouseX(Vector boxFingerPos) {
        if (xAxis == true)
            return (int) (screen.width * boxFingerPos.getX());
        else
            return xAxisInt;
    }

    private int robotMouseY(Vector boxFingerPos) {
        if (yAxis == true)
            return (int) (screen.height - boxFingerPos.getY() * screen.height);
        else
            return yAxisInt;
    }

    //setting the size the sensor is going to hover over(the mouse wont go over this size).
    public void setScreenSize(Dimension screen) {
        this.screen = screen;
    }

    //mapped keys. (incomplete)
    private void tappingScreen(Robot robot) {
//        System.out.println("click");
//        robot.mousePress(InputEvent.BUTTON1_MASK);
//        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.keyPress(KeyEvent.VK_SPACE);
        try {
            Thread.sleep(300 ); } catch (Exception e) {
        }
        robot.keyRelease(KeyEvent.VK_SPACE);
    }

    //mapping keys in case we need keystrokes instead of mouse movement.(incomplete)
//    private void handDirectionMappedKeys(Vector swipeDirection) {


//            if (clacDir(swipeDirection.getY() + 3) == 1 && swipeDirection.getX() < swipeDirection.getY()){ //up
//                robot.keyPress(KeyEvent.VK_W);
//                robot.keyRelease(KeyEvent.VK_W);
//            } else if ((clacDir(swipeDirection.getX() + 3) == -1) && (swipeDirection.getY() < swipeDirection.getX())) { //down
//                robot.keyPress(KeyEvent.VK_S);
//                robot.keyRelease(KeyEvent.VK_S);
//            } else if((clacDir(swipeDirection.getY() + 3) == -1) && (swipeDirection.getX() < swipeDirection.getY())) { //left
//                robot.keyPress(KeyEvent.VK_A);
//                robot.keyRelease(KeyEvent.VK_A);
//            } else if ((clacDir(swipeDirection.getX() + 3) == 1) && (swipeDirection.getY() < swipeDirection.getX())) { //right
//                robot.keyPress(KeyEvent.VK_D);
//                robot.keyRelease(KeyEvent.VK_D);
//            }

//        if ((clacDir(swipeDirection.getX() + 5) == 1)) { //right
//                robot.keyPress(KeyEvent.VK_RIGHT);
//                robot.keyRelease(KeyEvent.VK_RIGHT);
//        } else if ((clacDir(swipeDirection.getX() + 5) == 0)) { //left
//                robot.keyPress(KeyEvent.VK_LEFT);
//                robot.keyRelease(KeyEvent.VK_LEFT);



//        try {
//            Thread.sleep(10000);
//        } catch (Exception e) {
//        }


//    private int clacDir(double d) {
//        int temp = (int) d;
//        d = d - temp;
//        if (d >= .5)
//            d = 1;
//        else
//            d = 0;
//        return (int) d;
//    }

    // setting up the axises to be ready for the robot usage.
    public void setAxis(boolean xAxis, boolean yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public void setAxis(int xAxis, int yAxis) {
        this.xAxisInt = xAxis;
        this.yAxisInt = yAxis;
    }
}


public class LeapMouseForGames {
    CustomListenerAdjustable sensorListener;

    //constructors.
    public LeapMouseForGames(Dimension screen) {
        sensorListener = new CustomListenerAdjustable();
        sensorListener.setScreenSize(screen);

        start();
    }

    public LeapMouseForGames(Dimension screen, String axis, int value) {
        sensorListener = new CustomListenerAdjustable();
        sensorListener.setScreenSize(screen);

        if (axis.equals("x") == true) {
            sensorListener.setAxis(false, true);
            sensorListener.setAxis(value, 0);
        } else {
            sensorListener.setAxis(true, false);
            sensorListener.setAxis(0, value);
        }

        start();
    }

    public void start() {
        Controller c = new Controller();
        c.addListener(sensorListener);

        try {
            System.in.read();
        } catch (Exception e) {
        }

        c.removeListener(sensorListener);
    }
}
