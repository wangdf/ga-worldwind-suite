package keyframe;

public class Vector3 implements Vector<Vector3>
{
	public final static Vector3 ZERO = new Vector3(0, 0, 0);

	public double x;
	public double y;
	public double z;

	public Vector3()
	{
	}

	public Vector3(Vector3 v)
	{
		set(v);
	}

	public Vector3(double x, double y, double z)
	{
		set(x, y, z);
	}

	public Vector3 createNew()
	{
		return new Vector3();
	}

	@Override
	public Vector3 clone()
	{
		return new Vector3(this);
	}

	public Vector3 set(Vector3 v)
	{
		return set(v.x, v.y, v.z);
	}

	public Vector3 set(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vector3 mult(Vector3 v)
	{
		return mult(v, null);
	}

	public Vector3 mult(Vector3 v, Vector3 store)
	{
		if (store == null)
			store = new Vector3();
		store.x = x * v.x;
		store.y = y * v.y;
		store.z = z * v.z;
		return store;
	}

	public Vector3 multLocal(Vector3 v)
	{
		x *= v.x;
		y *= v.y;
		z *= v.z;
		return this;
	}

	public Vector3 mult(double s)
	{
		return mult(s, null);
	}

	public Vector3 mult(double s, Vector3 store)
	{
		if (store == null)
			store = new Vector3();
		store.x = x * s;
		store.y = y * s;
		store.z = z * s;
		return store;
	}

	public Vector3 multLocal(double s)
	{
		x *= s;
		y *= s;
		z *= s;
		return this;
	}

	public Vector3 divide(Vector3 v)
	{
		return divide(v, null);
	}

	public Vector3 divide(Vector3 v, Vector3 store)
	{
		if (store == null)
			store = new Vector3();
		store.x = x / v.x;
		store.y = y / v.y;
		store.z = z / v.z;
		return store;
	}

	public Vector3 divideLocal(Vector3 v)
	{
		x /= v.x;
		y /= v.y;
		z /= v.z;
		return this;
	}

	public Vector3 divide(double s)
	{
		return divide(s, null);
	}

	public Vector3 divide(double s, Vector3 store)
	{
		if (store == null)
			store = new Vector3();
		store.x = x / s;
		store.y = y / s;
		store.z = z / s;
		return store;
	}

	public Vector3 divideLocal(double s)
	{
		x /= s;
		y /= s;
		z /= s;
		return this;
	}

	public Vector3 add(Vector3 v)
	{
		return add(v, null);
	}

	public Vector3 add(Vector3 v, Vector3 store)
	{
		if (store == null)
			store = new Vector3();
		store.x = x + v.x;
		store.y = y + v.y;
		store.z = z + v.z;
		return store;
	}

	public Vector3 addLocal(Vector3 v)
	{
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}

	public Vector3 subtract(Vector3 v)
	{
		return subtract(v, null);
	}

	public Vector3 subtract(Vector3 v, Vector3 store)
	{
		if (store == null)
			store = new Vector3();
		store.x = x - v.x;
		store.y = y - v.y;
		store.z = z - v.z;
		return store;
	}

	public Vector3 subtractLocal(Vector3 v)
	{
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}

	public Vector3 max(Vector3 v)
	{
		return max(v, null);
	}

	public Vector3 max(Vector3 v, Vector3 store)
	{
		if (store == null)
			store = new Vector3();
		store.x = Math.max(x, v.x);
		store.y = Math.max(y, v.y);
		store.z = Math.max(z, v.z);
		return store;
	}

	public Vector3 maxLocal(Vector3 v)
	{
		x = Math.max(x, v.x);
		y = Math.max(y, v.y);
		z = Math.max(z, v.z);
		return this;
	}

	public Vector3 min(Vector3 v)
	{
		return min(v, null);
	}

	public Vector3 min(Vector3 v, Vector3 store)
	{
		if (store == null)
			store = new Vector3();
		store.x = Math.min(x, v.x);
		store.y = Math.min(y, v.y);
		store.z = Math.min(z, v.z);
		return store;
	}

	public Vector3 minLocal(Vector3 v)
	{
		x = Math.min(x, v.x);
		y = Math.min(y, v.y);
		z = Math.min(z, v.z);
		return this;
	}

	public double distanceSquared()
	{
		return x * x + y * y + z * z;
	}

	public double distance()
	{
		return Math.sqrt(distanceSquared());
	}

	public Vector3 zeroLocal()
	{
		x = 0d;
		y = 0d;
		z = 0d;
		return this;
	}

	public Vector3 negate()
	{
		return negate(null);
	}

	public Vector3 negate(Vector3 store)
	{
		if (store == null)
			store = new Vector3();
		store.x = -x;
		store.y = -y;
		store.z = -z;
		return store;
	}

	public Vector3 negateLocal()
	{
		x = -x;
		y = -y;
		z = -z;
		return this;
	}
}
