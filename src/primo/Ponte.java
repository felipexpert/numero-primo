package primo;

import java.util.List;

/**
 * Uma <code>Ponte</code> representa o elo entre os
 * objetos produtores e consumidores de informação.
 * Os <i>Produtores</i> utilizam as pontes para gravar
 * informações e compartilhar estas informações
 * com os <i>Consumidores</i> que lêem esses dados
 * na ponte para fazer o processamento
 * 
 * @author Felipe Carmona Miquilini
 * @author A
 * @author B
 * @version 1.3
 * @since 1.0
 * 
 * @see PonteNaoSincronizada
 * @see PonteSincronizada
 *
 */
public interface Ponte {
	/**
	 * Armazena as informações guardadas na Ponte. geralmente é usado pelas classes <i>Produtoras</i>
	 * @param valor
	 * @throws InterruptedException
	 */
	void set(long valor) throws InterruptedException;
	
	/**
	 * Lê as informações guardadas na Ponte. geralmente é usado pelas classes <i>Consumidoras</i>
	 * @return
	 * @throws InterruptedException
	 */
	List<Long> get() throws InterruptedException;
	
	void finalizar() throws InterruptedException;
	
	boolean isContinuar();

	boolean temNaPonte();
}
