package br.univali.game;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import br.univali.game.objects.DrawableObject;
import br.univali.game.objects.Enemy;
import br.univali.game.objects.GameObject;
import br.univali.game.objects.PlayerTank;
import br.univali.game.objects.Projectile;

public interface GameObjectCollectionInterface extends Remote {
	
	public void addEnemy(Enemy enemy) throws RemoteException;
	public void addProjectile(Projectile projectile) throws RemoteException;
	public void setTank(PlayerTank tank) throws RemoteException;
	public List<Enemy> getEnemies() throws RemoteException;
	public List<Projectile> getProjectiles() throws RemoteException;
	public PlayerTank getTank() throws RemoteException;
	public List<DrawableObject> getEffects() throws RemoteException;
	public List<DrawableObject> getPickups() throws RemoteException;
	public List<DrawableObject> getDrawableObjects() throws RemoteException;
	public void addEffect(DrawableObject effect) throws RemoteException;
	public void removeObject(GameObject object) throws RemoteException;
	public void removeEnemy(Enemy enemy) throws RemoteException;
	public void removeProjectile(Projectile projectile) throws RemoteException;
	public void clear() throws RemoteException;
	public void addPickup(DrawableObject pickup) throws RemoteException;
	public void removePickup(DrawableObject pickup) throws RemoteException;
}
