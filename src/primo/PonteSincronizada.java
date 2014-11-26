package primo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PonteSincronizada implements Ponte{
	
	private boolean continuar = true;
	private boolean ocupado = false;
	
	private LinkedList<Long> valores = new LinkedList<>();
	//private boolean ocupado = false;
	
	@Override
	public synchronized void set(long valor) throws InterruptedException {
		//while(ocupado) {
		//	wait();
		//}
		
		
		
			valores.offer(valor);
			
			ocupado = true;
			notifyAll();
			
		
			
		
		/*
		if(count == 60) {
			count = 0;
			Thread.sleep(1);
		}*/
	}
	
	@Override
	public synchronized List<Long> get() throws InterruptedException {
		while (!ocupado) {
			wait();
		}
		
		List<Long> lista = new ArrayList<>();
		//valores = new ArrayList<>();
		while(!valores.isEmpty()) {
			lista.add(valores.poll());
		}
		
		ocupado = false;
		//notifyAll();
		return lista;
		
	}

	public boolean temNaPonte() {
		return !valores.isEmpty();
	}
	
	@Override
	public boolean isContinuar() {
		return continuar;
	}

	@Override
	public synchronized void finalizar() throws InterruptedException {
		//while(ocupado) {
		//	wait();
		//}
		
		if(valores.size() > 0) {
			ocupado = true;
			notifyAll();
		}
		continuar = false;
		
	}

}
