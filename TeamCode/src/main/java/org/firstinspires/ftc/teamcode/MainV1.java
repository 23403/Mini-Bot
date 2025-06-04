package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
@Config
public class MainV1 extends LinearOpMode {
    public static double taFF = 0.18;
    public static double eaFF = 0.18;
    public static int taCpos = 0;
    public static int eaCpos = 0;
    public static double DEADZONE = 0;
    public static double eaSpeed = 0.8;

    // todo: write your code here
    @Override
    public void runOpMode() {

        DcMotorEx extendArm = hardwareMap.get(DcMotorEx.class, "Extend");
        DcMotorEx arm = hardwareMap.get(DcMotorEx.class, "arm");
        DcMotor chassisBL = hardwareMap.get(DcMotor.class, "chassisBL");
        DcMotor chassisBR = hardwareMap.get(DcMotor.class, "chassisBR");
        DcMotor chassisFL = hardwareMap.get(DcMotor.class, "chassisFL");
        DcMotor chassisFR = hardwareMap.get(DcMotor.class, "chassisFR");
        CRServo clawL = hardwareMap.get(CRServo.class, "clawL");
        CRServo clawR = hardwareMap.get(CRServo.class, "clawR");
        chassisBL.setDirection(DcMotor.Direction.REVERSE);
        chassisFL.setDirection(DcMotor.Direction.REVERSE);
        extendArm.setDirection(DcMotorSimple.Direction.REVERSE);
        arm.setDirection(DcMotorSimple.Direction.REVERSE);
        clawL.setDirection(CRServo.Direction.REVERSE);
        extendArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // init
        waitForStart();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                // loop
                // drive
                double forward = -gamepad1.left_stick_y;//forward
                double strafe = gamepad1.left_stick_x;//strafe
                double turn = gamepad1.right_stick_x;//rotation
                //formula
                double chassisFLPower = (forward + strafe + turn);
                double chassisBLPower = (forward - strafe + turn);
                double chassisFRPower = -(forward - strafe - turn);
                double chassisBRPower = (forward + strafe - turn);
                //power
                chassisFL.setPower(chassisFLPower);
                chassisBL.setPower(chassisBLPower);
                chassisFR.setPower(chassisFRPower);
                chassisBR.setPower(chassisBRPower);
                // arm code
                if (gamepad2.right_stick_y > DEADZONE) {
                    arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    arm.setPower(clamp(gamepad2.right_stick_y + taFF));
                    taCpos = arm.getCurrentPosition();
                } else if (gamepad2.right_stick_y < DEADZONE) {
                    arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    arm.setPower(clamp(gamepad2.right_stick_y));
                    taCpos = arm.getCurrentPosition();
                } else {
                    arm.setPower(taFF);
                    arm.setTargetPosition(taCpos);
                    arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
                // sliders code
                if (gamepad2.dpad_up) {
                    extendArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    extendArm.setPower(eaSpeed + eaCpos);
                    eaCpos = extendArm.getCurrentPosition();
                } else if (gamepad2.dpad_down) {
                    extendArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    extendArm.setPower(-eaSpeed);
                    eaCpos = extendArm.getCurrentPosition();
                } else {
                    extendArm.setPower(eaFF);
                    extendArm.setTargetPosition(eaCpos);
                    extendArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
                // claw code
                if (gamepad1.right_bumper) {
                    clawL.setPower(1);
                    clawR.setPower(1);
                } else if (gamepad1.left_bumper) {
                    clawL.setPower(-1);
                    clawR.setPower(-1);
                } else {
                    clawL.setPower(0);
                    clawR.setPower(0);
                }
            }
        }
    }
    private double clamp(double val) {
        return Math.max(-1, Math.min(1, val));
    }
}
