package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.IMU;

@TeleOp

public class MainV1_test extends LinearOpMode {
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


    // todo: write your code here
    @Override
    public void runOpMode() {

        DcMotor arm = hardwareMap.get(DcMotor.class, "arm");
        DcMotor chassisBL = hardwareMap.get(DcMotor.class, "chassisBL");
        DcMotor chassisBR = hardwareMap.get(DcMotor.class, "chassisBR");
        DcMotor chassisFL = hardwareMap.get(DcMotor.class, "chassisFL");
        DcMotor chassisFR = hardwareMap.get(DcMotor.class, "chassisFR");
        CRServo clawL = hardwareMap.get(CRServo.class, "clawL");
        CRServo clawR = hardwareMap.get(CRServo.class, "clawR");
        chassisBL.setDirection(DcMotor.Direction.REVERSE);
        chassisFL.setDirection(DcMotor.Direction.REVERSE);
        clawL.setDirection(CRServo.Direction.REVERSE);
        // init
        waitForStart();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                // loop
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
                if (gamepad1.dpad_up) {
                    arm.setPower(0.7);
                } else if (gamepad1.dpad_down) {
                    arm.setPower(-0.5);
                } else {
                    arm.setPower(0);
                }
                //claw
                if (gamepad1.right_bumper) {
                    clawL.setPower(1);
                    clawR.setPower(1);
                } else {
                    clawL.setPower(0);
                    clawR.setPower(0);
                }
            }


        }
    }
}
