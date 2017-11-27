package slfoster_metrejo;


import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

/**
 * HawkinsLab - a robot by Meagan Trejo and Samuel Foster
 */
public class HawkinsLab extends AdvancedRobot {

	private double fieldW, fieldH;
	private double x, y;
	private byte direction = 1;

	private final double PI = Math.PI;

	private ArrayList<GravityPoint> gravityList;
	private HashMap<String, Enemy> enemys;
	private BountySystem hunter;

	/**
	 * run: MyFirstRobot's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here
		fieldW = this.getBattleFieldWidth();
		fieldH = this.getBattleFieldWidth();
		gravityList = new ArrayList<GravityPoint>();
		enemys = new HashMap<String, Enemy>();
		hunter = new BountySystem();

		// Origin attraction
		gravityList.add(new GravityPoint("origin", fieldW / 2, fieldH / 2, 5));

		setColors(Color.getHSBColor(15, 50, 50), Color.blue, Color.green); // body,gun,radar

		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);

		Random r = new Random();
		int randomCounter = 0;

		turnRadarRightRadians(2 * PI);

		while (true) {
			scanRadar();
			
			doMove();
			shoot();
			// carry out all the queued up actions

			execute();

		}

	}

	public void onHitWall(HitWallEvent e) {
		direction *= -1;
	}

	public void onHitRobot(HitRobotEvent e) {
		direction *= -1;
	}

	private void doMove() {
		Enemy t = hunter.getTarget();
		double tDist = dist(getX(), getY(), t.getX(), t.getY());
		if (getVelocity() == 0)
			direction *= -1;

		// spiral toward our enemy
		setTurnRight(normBearing(t.getBearing() + 90 - (15 * direction)));
		setAhead(tDist * direction);
	}

	private void shoot() {
		Enemy enemy = hunter.getTarget();
		double enDist = dist(getX(), getY(), enemy.getX(), enemy.getY());

		double firePower = Math.min(500 / enDist, 3);
		double bulletSpeed = 20 - firePower * 3;
		long time = (long) (enDist / bulletSpeed);

		double futureX = enemy.getFutureX(time);
		double futureY = enemy.getFutureY(time);
		double absDeg = absbearing(getX(), getY(), futureX, futureY) * (180 / PI);
		setTurnGunRight(normBearing(absDeg - getGunHeading()));

		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
			setFire(firePower);
		}
	}

	public void scanRadar() {
		turnRadarLeftRadians(2 * PI);
	}

	public void antiGravity() {
		double forceX = 0, forceY = 0;
		double force, angle;

		for (GravityPoint point : gravityList) {
			force = point.force(dist(getX(), getY(), point.getX(), point.getY()));
			// Angle between bot and point
			angle = normBearing(PI / 2 - Math.atan2(getY() - point.getY(), getX() - point.getX()));
			forceX += Math.sin(angle) * force;
			forceY += Math.cos(angle) * force;
		}

		forceX += 5000 / Math.pow(dist(getX(), getY(), getBattleFieldWidth(), getY()), 3);
		forceX -= 5000 / Math.pow(dist(getX(), getY(), 0, getY()), 3);
		forceY += 5000 / Math.pow(dist(getX(), getY(), getX(), getBattleFieldHeight()), 3);
		forceY -= 5000 / Math.pow(dist(getX(), getY(), getX(), 0), 3);

		headFor(getX() - forceX, getY() - forceY);
	}

	public void antiGravity(double specialX, double specialY) {
		double forceX = 0, forceY = 0;
		double force, angle;

		for (GravityPoint point : gravityList) {
			force = point.force(dist(getX(), getY(), point.getX(), point.getY()));
			// Angle between bot and point
			angle = normBearing(PI / 2 - Math.atan2(getY() - point.getY(), getX() - point.getX()));
			forceX += Math.sin(angle) * force;
			forceY += Math.cos(angle) * force;
		}

		forceX += 5000 / Math.pow(dist(getX(), getY(), getBattleFieldWidth(), getY()), 3);
		forceX -= 5000 / Math.pow(dist(getX(), getY(), 0, getY()), 3);
		forceY += 5000 / Math.pow(dist(getX(), getY(), getX(), getBattleFieldHeight()), 3);
		forceY -= 5000 / Math.pow(dist(getX(), getY(), getX(), 0), 3);

		headFor(specialX - forceX, specialY - forceY);
	}

	double normBearing(double angle) {
		if (angle > PI) {
			angle -= 2 * PI;
		} else if (angle < -PI) {
			angle += 2 * PI;
		}
		return angle;
	}

	public double absbearing(double x, double y, double x2, double y2) {
		double xDiff = x2 - x;
		double yDiff = y2 - y;
		double distance = dist(x, y, x2, y2);
		if (xDiff > 0 && yDiff > 0) {
			return Math.asin(xDiff / distance);
		} else if (xDiff > 0 && yDiff < 0) {
			return PI - Math.asin(xDiff / distance);
		} else if (xDiff < 0 && yDiff < 0) {
			return PI + Math.asin(-xDiff / distance);
		} else if (xDiff < 0 && yDiff > 0) {
			return 2.0 * PI - Math.asin(-xDiff / distance);
		}
		return 0;
	}

	public void headFor(double x, double y) {
		setAhead(200 * turnTo(Math.toDegrees(absbearing(getX(), getY(), x, y))));
	}

	public int turnTo(double angle) {
		int direction;
		double ang = normBearing(getHeading() - angle);
		if (ang > 90) {
			ang -= 180;
			direction = -1;
		} else if (ang < -90) {
			ang += 180;
			direction = -1;
		} else {
			direction = 1;
		}
		setTurnLeft(ang);
		return direction;
	}

	public double dist(double x, double y, double x2, double y2) {
		double xDiff = x2 - x;
		double yDiff = y2 - y;
		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}

	public void move(int amount) {
		ahead(amount * direction);
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {

		double absoluteBearingRadians = (getHeadingRadians() + e.getBearingRadians()) % (2 * PI);
		double x = getX() + Math.sin(absoluteBearingRadians) * e.getDistance();
		double y = getY() + Math.cos(absoluteBearingRadians) * e.getDistance();

		if (enemys.containsKey(e.getName())) {
			Enemy target = enemys.get(e.getName());
			hunter.updateTarget(e.getName(), x, y, e.getEnergy(), e.getBearing(), e.getHeading(), e.getVelocity());

			System.out.println(target);

			int ind = target.getGravityIndex();
			gravityList.get(ind).setX(x);
			gravityList.get(ind).setY(y);
		} else {
			// Create new gravity point and target
			gravityList.add(new GravityPoint(e.getName(), x, y, -2));
			Enemy en = new Enemy(e.getName(), x, y, e.getEnergy(), e.getBearing(), e.getHeading(), e.getVelocity());
			en.setGravityIndex(gravityList.size() - 1);
			enemys.put(e.getName(), en);
			hunter.addTarget(en);

			System.out.print("ADDED ENEMY: ");
			System.out.println(en);
		}
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		move(50);
	}

	/**
	 * onHitWall: What to do when you hit a wall
	 */
}

class BountySystem {
	private HashMap<String, Enemy> targets;

	public BountySystem() {
		targets = new HashMap<String, Enemy>();
	}

	public void update() {
		for (Entry<String, Enemy> entry : targets.entrySet()) {
			Enemy a = entry.getValue();

			double points = 0;
			// Damage potential
			points = a.getEnergy();

			// Kill bonus potential
			points += a.getDamageDone() * 0.20;

			a.setPointValue(points);

			a.updatePos(a.getFutureX(10), a.getFutureY(10));
		}

	}

	public void addTarget(Enemy e) {
		targets.put(e.getName(), e);
	}

	public void updateTarget(String name, double x, double y, double energy, double bearing, double heading, double velocity) {
		Enemy t = targets.get(name);
		t.updatePos(x, y);
		t.updateEnergy(energy);
		t.updateBearing(bearing);
		t.updateHeading(heading);
		t.updateVelocity(velocity);
	}

	public Enemy getTargetByName(String name) {
		return targets.get(name).copy();
	}

	public Enemy getTarget() {
		double points = -1;
		Enemy target = new Enemy("", 0, 0, 0, 0, 0, 0);

		for (Entry<String, Enemy> entry : targets.entrySet()) {
			Enemy candidate = entry.getValue();

			if (candidate.getPointValue() >= points) {
				points = entry.getValue().getPointValue();
				target = candidate;
			}
		}

		System.out.println("BOUNTY: " + target);

		return target;
	}

}

class Enemy {
	private double x, y;
	private double energy, bearing, heading, velocity;
	private double damageDone = 0;
	private String name;
	private double pointValue;
	private int gravIndex;

	public Enemy(String name, double x, double y, double energy, double bearing, double heading, double velocity) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.energy = energy;
		this.bearing = bearing;
		this.heading = heading;
		this.velocity = velocity;
	}

	public void updateHeading(double heading) {
		this.heading = heading;
	}

	public void updateVelocity(double velocity) {
		this.velocity = velocity;
	}

	public double getFutureY(long time) {
		return y + Math.cos(Math.toRadians(heading)) * velocity * time;
	}

	public double getFutureX(long time) {
		return x + Math.sin(Math.toRadians(heading)) * velocity * time;
	}

	public void updateBearing(double bearing) {
		this.bearing = bearing;
	}

	public double getBearing() {
		return bearing;
	}

	public double getY() {
		return y;
	}

	public double getX() {
		return x;
	}

	public void setGravityIndex(int index) {
		gravIndex = index;
	}

	public int getGravityIndex() {
		return gravIndex;
	}

	public double getEnergy() {
		return energy;
	}

	public void updateEnergy(double energy) {
		this.energy = energy;
	}

	public void setDamageDone(double damage) {
		damageDone = damage;
	}

	public double getDamageDone() {
		return damageDone;
	}

	public Enemy copy() {
		return new Enemy(name, x, y, energy, bearing, heading, velocity);
	}

	public String getName() {
		return name;
	}

	public double getPointValue() {
		return pointValue;
	}

	public void setPointValue(double pointValue) {
		this.pointValue = pointValue;
	}

	public void updatePos(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		String s = "Robot: " + name + " | Energy: " + energy + " | Worth: " + pointValue + " | X: " + x + " , Y: " + y;
		return s;
	}
}

class GravityPoint {
	private double x, y;
	private double strength;
	private String name;

	public GravityPoint(String name, double x, double y, double strength) {
		this.x = x;
		this.y = y;
		this.strength = strength;
		this.name = name;
	}

	public double force(double distance) {
		return this.strength / Math.pow(distance, 2);
	}

	public String getName() {
		return name;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getStrength() {
		return strength;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setStrength(double strength) {
		this.strength = strength;
	}
}