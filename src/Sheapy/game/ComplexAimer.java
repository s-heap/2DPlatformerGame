package Sheapy.game;

import Sheapy.maths.Vector3f;

public class ComplexAimer {
	static float p = Projectile.getPower();
	static float a = Constants.worldAcceleration;

	public static float aim(Vector3f displacment, Vector3f velocity) {
		//double t = 0;

		double x = displacment.x;
		double y = displacment.y;
		double i = velocity.x;
		double j = velocity.y;

		double $159 = i * x + j * y;
		double $160 = Math.pow(x, 2) + Math.pow(y, 2);
		double $161 = Math.pow(i, 2) + Math.pow(j, 2) - Math.pow(p, 2) - a * y;
		double $162 = 1152 * Math.pow(a, 2) * $161 * $160;
		double $163 = -1728 * Math.pow(a, 2) * Math.pow(j, 2) * $160;
		double $164 = -1728 * Math.pow(a, 2) * Math.pow($159, 2);
		double $165 = -1152 * a * j * $161 * $159;
		double $166 = 16 * Math.pow($161, 2) + 96 * a * j * $159 + 48 * Math.pow(a, 2) * $160;
		double $158 = -128 * Math.pow($161, 3) + $165 + $164 + $163 + $162;
		double $167 = $158 + Math.sqrt(-4 * Math.pow($166, 3) + Math.pow($158, 2));
		double $168 = -$161;
		double $169 = -(0.41997368329829105 * $166) / (Math.pow(a, 2) * Math.cbrt($167));
		double $170 = (4 * Math.pow(j, 2) + 1.33333333333333333 * $161 + 4 * $168 - 0.26456684199469993 * Math.cbrt($167)) / Math.pow(a, 2) + $169;
		double $171 = (0.41997368329829105 * $166) / (Math.pow(a, 2) * Math.cbrt($167));
		double $172 = (64 * Math.pow(j, 3) + 64 * j * $168) / Math.pow(a, 3) - (64 * $159) / Math.pow(a, 2);
		double $173 = (8 * Math.pow(j, 2) - 1.33333333333333333 * $161 + 4 * $168 + 0.26456684199469993 * Math.cbrt($167)) / Math.pow(a, 2) + $171 + (0.25 * $172) / Math.sqrt($170);
		double $174 = (8 * Math.pow(j, 2) - 1.33333333333333333 * $161 + 4 * $168 + 0.26456684199469993 * Math.cbrt($167)) / Math.pow(a, 2) + $171 - (0.25 * $172) / Math.sqrt($170);

		double t1 = j / a - 0.5 * Math.sqrt($170) - 0.5 * Math.sqrt($174);
		double t2 = j / a - 0.5 * Math.sqrt($170) + 0.5 * Math.sqrt($174);
		double t3 = j / a - 0.5 * Math.sqrt($170) - 0.5 * Math.sqrt($173);
		double t4 = j / a - 0.5 * Math.sqrt($170) + 0.5 * Math.sqrt($173);
		System.out.println(t1 + " " + t2 + " " + t3 + " " + t4);

		return (float) t1;

//		double s = (Math.pow(j, 2) - Math.pow(p, 2) + a * y - 1);
//		double d = Math.pow(x, 2) + Math.pow(y, 2);
//		double v = x - i * j * y;
//		double w = -((64 * Math.pow(j, 3)) / Math.pow(a, 3)) + ((64 * s * j) / Math.pow(a, 3)) - ((64 * i * v) / Math.pow(a, 2));
//		double m = 128 * Math.pow(s, 3) - 1152 * i * a * j * v * s - 1152 * Math.pow(a, 2) * d * s - 1728 * Math.pow(a, 2) * Math.pow(v, 2) + 1728 * Math.pow(a, 2) * Math.pow(j, 2) * d;
//		double l = 48 * p * Math.pow(a, 2) - 96 * i * j * v * a + 16 * Math.pow(s, 2);
//		double n = (3 * Math.pow(a, 2) * Math.cbrt(2)) * Math.cbrt(Math.cbrt(m + Math.sqrt(Math.pow(m, 2) - 4 * Math.pow(l, 3))) - ((l * Math.cbrt(2)) / (3 * Math.pow(a, 2) * Math.cbrt(m + Math.sqrt(Math.pow(m, 2) - 4 * Math.pow(l, 3))))));
//
//		double s1 = w / (4 * Math.sqrt(((4 * Math.pow(j, 2)) / Math.pow(a, 2)) + ((64 * s) / Math.pow(a, 3)) + (1 / n)));
//		// sign after n changes in equation variation
//		double s2 = 1 / (n + s1);
//		double s3 = 0.5 * Math.sqrt(((8 * Math.pow(j, 2)) / Math.pow(a, 2)) - ((16 * s) / (3 * Math.pow(a, 2))) - (s2));
//		// sign after n changes in equation variation
//		double s4 = 1 / (n - s3);
//
//		// sign before 0.5 changes in equation variation
//		t = -(j / a) + 0.5 * Math.sqrt(((4 * Math.pow(j, 2) / Math.pow(a, 2))) - ((8 * s) / (3 * Math.pow(a, 2))) + s4);
//
//		return (float) t;

		// Sign change variations (top to bottom above):
		// - - - (Negative)
		// - + - (Negative)
		// + - +
		// + + +

		// double s = Math.pow(i, 2) + Math.pow(j, 2) - Math.pow(p, 2) - a * y;
		// double n = i * x + j * y;
		// double m = Math.pow(x, 2) + Math.pow(y, 2);
		// double l = 16 * Math.pow(s, 2) + 96 * a * j * n + 48 * m * Math.pow(a, 2);
		// double k = -128 * Math.pow(s, 3) - 1152 * a * j * s * n - 1728 * Math.pow(a, 2) * Math.pow(n, 2) - 1728 * m * Math.pow(a, 2) * Math.pow(j, 2) + 1152 * s * m * Math.pow(a, 2);
		// double z = Math.cbrt(k + Math.sqrt(-4 * Math.pow(l, 3) + Math.pow(k, 2)));
		// double h = (-4 * s) / Math.pow(a, 2) - (0.419974 * l) / (z * Math.pow(a, 2));
		// double v = (4 * Math.pow(j, 2)) / Math.pow(a, 2) + (1.33333 * s) / Math.pow(a, 2) + h - (1 / (Math.pow(a, 2))) * 0.264567 * z;
		// double e = 0.25 * ((64 * Math.pow(j, 3)) / Math.pow(a, 3) + (-64 * j * s) / Math.pow(a, 3) - (64 * n) / Math.pow(a, 2));
		// double q = (8 * Math.pow(j, 2)) / Math.pow(a, 2) - (1.33333 * s) / Math.pow(a, 2) + (-4 * s) / Math.pow(a, 2) + (0.419974 * l) / (z * Math.pow(a, 2)) + (1 / (Math.pow(a, 2))) * 0.264567 * z - e / Math.sqrt(v);
		// t = j / a - 0.5 * Math.sqrt(v) - 0.5 * Math.sqrt(q);
		// return (float) t;

	}
}
