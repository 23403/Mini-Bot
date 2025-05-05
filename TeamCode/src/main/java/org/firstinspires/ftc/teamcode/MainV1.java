package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;

import xyz.nin1275.constants.PIDFKCoefficients;
import xyz.nin1275.controllers.PID;
import xyz.nin1275.subsystems.SlidesSS;

@TeleOp

public class MainV1 extends LinearOpMode {
    private DcMotor arm;
    private Blinker control_Hub;
    private Blinker expansion_Hub_2;
    private DcMotor extend;
    private DcMotor chassisBL;
    private DcMotor chassisBR;
    private DcMotor chassisFL;
    private DcMotor chassisFR;
    private CRServo clawL;
    private CRServo clawR;
    private IMU imu;
    public static PIDFKCoefficients eaPID = new PIDFKCoefficients(
            0.02,
            0,
            0,
            0.08
    );
    public static PIDFKCoefficients taPID = new PIDFKCoefficients(
            0.03,
            0,
            0,
            0.18
    );
    public static double CPR = 537.7; // counts per revolution
    public static double INCHES_PER_REV = 16; // how far the arm travels linearly per motor revolution

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
        clawL.setDirection(CRServo.Direction.REVERSE);
        PID eaController = new PID(Math.sqrt(eaPID.P), eaPID.I, eaPID.D);
        PID taController = new PID(Math.sqrt(taPID.P), taPID.I, taPID.D);
        SlidesSS sliders = new SlidesSS(extendArm, eaController, 0, eaPID.F, CPR, INCHES_PER_REV, true);
        SlidesSS armSS = new SlidesSS(arm, taController, 0, taPID.F, CPR, INCHES_PER_REV, true);

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
                sliders.update(gamepad1.dpad_up, gamepad1.dpad_down);
                armSS.update(gamepad1.right_stick_y > 0.3, gamepad1.right_stick_y < 0.3);
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
                if (gamepad1.left_bumper & gamepad1.right_bumper) {
                    clawL.setPower(0);
                    clawR.setPower(0);
                }
            }


        }
    }
}
