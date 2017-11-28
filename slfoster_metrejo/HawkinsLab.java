package slfoster_metrejo;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

/**
 * HawkinsLab - a robot by Meagan Trejo and Samuel Foster
 */
public class HawkinsLab extends AdvancedRobot {

	boolean looking;
	boolean scannedWeakling;
	double moveAmount;
	int count = 0;
	int distCount = 0;
	double gunTurnAmt;
	String trackName;

	public void run() {
		setColors(Color.getHSBColor(210, 0.03f, 0.54f), Color.getHSBColor(240, 0.02f, 0.16f), Color.getHSBColor(0, 0, 0.95f));
		setBulletColor(Color.getHSBColor(20, .20f, .20f));
		moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());

		looking = false;
		scannedWeakling = false;
		trackName = null;
		
		gunTurnAmt = 10;
		
		turnLeft(getHeading() % 90);
		ahead(moveAmount);
		looking = true;
		turnGunRight(90);
		turnRight(90);

		while (true) {
			looking = true;
			ahead(moveAmount);

			looking = false;
			turnRight(90);
		}
	}

	public void onHitRobot(HitRobotEvent e) {
		if (Math.abs(e.getBearing()) < 90) {
			back(200);
		} else {
			ahead(200);
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		fire(2);
		
		if (!scannedWeakling) {
			if (e.getDistance() < 40 && e.getEnergy() < 30 && getEnergy() > e.getEnergy()) {
				scannedWeakling = true;
				trackName = e.getName();
				System.out.println("HUNTING " + e.getName());
			}
		} else {
			if (trackName != null && !e.getName().equals(trackName)) {
				scannedWeakling = false;
			}

			if (trackName == null) {
				trackName = e.getName();
				scannedWeakling = false;
				out.println("Tracking " + trackName); // should only print once,
														// but prints repeatedly
			}

			if (e.getDistance() > 150) {
				gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
				setTurnGunRight(gunTurnAmt);
				++distCount;
				if (distCount == 5) {
					fire(5);
				}
				fire(1); // misses often
				turnRight(e.getBearing());
				ahead(e.getDistance() - 50);
			} else {
				gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + getHeading() - getRadarHeading());
				turnGunRight(gunTurnAmt);
				// fire(3);

				if (e.getDistance() < 100) {
					if (e.getEnergy() < 40) {
						turnRight(e.getBearing());
						out.println("Gonna RAM " + trackName + "!!!");
						ahead(e.getDistance() + 1);
						if (e.getEnergy() > 10) {
							fire(2);
						}
					}

					else if (e.getBearing() > -90 && e.getBearing() <= 90) {
						back(40);
						fire(3);
					} else {
						ahead(40);
						fire(3);
					}
				} else {
					fire(3);
				}
				scan();
			}
		}

		

		if (looking) {
			scan();
		}

	}
}
