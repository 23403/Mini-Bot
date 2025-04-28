package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import xyz.nin1275.constants.PIDFKCoefficients;

@Config("PID Tune Slides")
@Autonomous(name="PID Tune Slides", group="test_ftc23403")
public class PIDTuneSlides extends OpMode {
    private DcMotorEx extendArm1;
    private DcMotorEx turnArm;
    private PIDController eaController;
    private PIDController taController;
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
    public static double eaTARGET = 0;
    public static double taTARGET = 0;
    public static double CPR = 537.7; // counts per revolution
    public static double INCHES_PER_REV = 16; // how far the arm travels linearly per motor revolution


    /**
     * Initialization code.
     */
    @Override
    public void init() {
        // set the PID values
        eaController = new PIDController(Math.sqrt(eaPID.P), eaPID.I, eaPID.D);
        taController = new PIDController(Math.sqrt(taPID.P), taPID.I, taPID.D);
        // hardware
        extendArm1 = hardwareMap.get(DcMotorEx.class, "Extend");
        turnArm = hardwareMap.get(DcMotorEx.class, "arm");
        // reverse
        extendArm1.setDirection(DcMotorSimple.Direction.REVERSE);
        // reset encoders
        extendArm1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turnArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // turn on the motors without the built in controller
        extendArm1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        turnArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // combine both FTCDashboard and the regular telemetry
        telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        // telemetry
        telemetry.addLine("Use this to tune the arm and slides.");
        telemetry.update();
    }

    /**
     * This updates the FTC Dashboard telemetry with the ERROR values and target pos and current pos for easy tuning and debugging!
     */
    @Override
    public void loop() {
        // Update PID values
        eaController.setPID(Math.sqrt(PIDTuneSlides.eaPID.P), PIDTuneSlides.eaPID.I, PIDTuneSlides.eaPID.D);
        taController.setPID(Math.sqrt(PIDTuneSlides.taPID.P), PIDTuneSlides.taPID.I, PIDTuneSlides.taPID.D);
        // Get current positions
        int eaTicks = extendArm1.getCurrentPosition();
        int taTicks = turnArm.getCurrentPosition();
        // Convert ticks to inches
        double eaInches = (eaTicks / CPR) * INCHES_PER_REV;
        double taInches = (taTicks / CPR) * INCHES_PER_REV;
        // Calculate PID only on one motor (leader)
        double eaPid = eaController.calculate(eaInches, eaTARGET);
        double taPid = taController.calculate(taInches, taTARGET);
        double eaFF = Math.cos(Math.toRadians(eaTARGET/CPR)) * eaPID.F;
        double taFF = Math.cos(Math.toRadians(taTARGET/CPR) + Math.cos(Math.toRadians(eaTARGET/CPR))) * taPID.F;
        double eaRawPower = eaPid + eaFF;
        double taRawPower = taPid + taFF;
        // Apply power
        extendArm1.setPower(Math.max(-1, Math.min(1, eaRawPower))); // leader
        turnArm.setPower(Math.max(-1, Math.min(1, taRawPower))); // follower with correction
        // telemetry for debugging
        telemetry.addData("slides PIDF", "P: " + eaPID.P + " I: " + eaPID.I + " D: " + eaPID.D + " F: " + eaPID.F);
        telemetry.addData("arm PIDF", "P: " + taPID.P + " I: " + taPID.I + " D: " + taPID.D + " F: " + taPID.F);
        telemetry.addData("arm target", taTARGET);
        telemetry.addData("slides target", eaTARGET);
        telemetry.addData("eaCpos", eaInches);
        telemetry.addData("taCpos2", taInches);
        telemetry.addData("eaPower", eaRawPower);
        telemetry.addData("taPower", taRawPower);
        telemetry.addData("eaError", Math.abs(eaTARGET - eaInches));
        telemetry.addData("taError", Math.abs(taTARGET - taInches));
        telemetry.update();
    }
}
