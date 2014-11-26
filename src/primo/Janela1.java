package primo;


import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import primo.Ponte;
import primo.PonteSincronizada;

@SuppressWarnings("serial")
public class Janela1 extends JFrame implements ActionListener, WindowListener{
	private JButton ok, fechar, negrito, normal, relogar, inserePrimos, cancelarOperacao, abrir, salvar;
	private JPanel panel1, panel2;
	private JLabel info;
	private JTextArea texto;
	private Ponte ponte;
	
	
	public Janela1(String usuario) {
		super("Bem vindo(a) " + usuario);
		ponte = new PonteSincronizada();
		setSize(700, 500);
		texto = new JTextArea();
		texto.setFont(new Font("serif", Font.PLAIN, 26));
		
		JScrollPane sp = new JScrollPane(texto);
		
		Font f = new Font("arial", Font.PLAIN, 16);
		
		ok = new JButton("Simular Impressão");
		ok.setFont(f);
		ok.addActionListener(this);
		fechar = new JButton("Fechar");
		fechar.setFont(f);
		fechar.addActionListener(this);
		negrito = new JButton("Deixar negrito");
		negrito.setFont(f);
		negrito.addActionListener(this);
		normal = new JButton("Deixar fonte normal");
		normal.setFont(f);
		normal.addActionListener(this);
		relogar = new JButton("Relogar");
		relogar.setFont(f);
		relogar.addActionListener(this);
		inserePrimos = new JButton("Inserir primos");
		inserePrimos.setToolTipText("Com este utilitário, procuraremos todos os números " + 
									"primos entre zero e o número positivo que você inserir " + 
									"(incluindo o número inserido)");
		inserePrimos.setFont(f);
		inserePrimos.addActionListener(this);
		cancelarOperacao = new JButton("Cancelar cálculo");
		cancelarOperacao.setFont(f);
		cancelarOperacao.addActionListener(this);
		abrir = new JButton("Abrir");
		abrir.setFont(f);
		abrir.addActionListener(this);
		salvar = new JButton("Salvar");
		salvar.setFont(f);
		salvar.addActionListener(this);
		
		panel1 = new JPanel(new GridLayout(2, 4));
		panel1.add(inserePrimos);
		panel1.add(ok);
		panel1.add(negrito);
		panel1.add(normal);
		//panel.add(cancelarOperacao);
		panel1.add(abrir);
		panel1.add(salvar);
		panel1.add(relogar);
		panel1.add(fechar);
		
		panel2 = new JPanel(new FlowLayout());
		
		info = new JLabel();
		info.setFont(f);
		
		panel2.add(info);
		panel2.add(cancelarOperacao);
		
		
		
		add("Center", sp);
		add("North", panel1);
		add("South",panel2);
		panel2.setVisible(false);
		
		
		addWindowListener(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);		
		setVisible(true);
	}
	
	JTextArea getTexto() {
		return texto;
	}
	
	public JPanel getPanel1() {
		return panel1;
	}
	
	public JPanel getPanel2() {
		return panel2;
	}
	
	public static void main(String[] args) {
		//Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		inicializa();
		/*
		PonteSincronizada ponte = new PonteSincronizada();
		new Thread(new Consumidor(ponte, null)).start();
		new Thread(new Produtor(ponte, 100000)).start();
		*/
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == ok) {
			try {
				JOptionPane.showMessageDialog(this, texto.getText(), "Que bacana", 1);
			} catch(java.lang.OutOfMemoryError e1) {
				JOptionPane.showMessageDialog(this, "Texto muito grande, erro ao simular impressão", "Erro", JOptionPane.ERROR_MESSAGE);
			}
		} else if(e.getSource() == negrito) {
			texto.setFont(new Font("Serif", Font.BOLD, 26));
		} else if(e.getSource() == normal){
			texto.setFont(new Font("Serif", Font.PLAIN, 26));
		} else if(e.getSource() == relogar) {
			if(JOptionPane.showConfirmDialog(this, "Deseja realmente relogar?", "Atenção", JOptionPane.YES_NO_OPTION) == 0) {
				this.dispose();
				inicializa();
			}
		} else if(e.getSource() == inserePrimos) {
			String t = JOptionPane.showInputDialog(this, "Insira o numero máximo para que encontremos todos os números primos", "Por favor", 1);
			if(t != null) {
				panel2.setVisible(true);
				panel1.setVisible(false);
				try {
					long maxPrimo = Long.parseLong(t);
					if(maxPrimo > 1) {
						if(maxPrimo > 1_000_000) {
							if(JOptionPane.showConfirmDialog(this, "Você inseriu um número bem grande, deseja realmente efetuar esta\noperação, mesmo sabendo que poderá levar alguns minutos\ndependendo da potencia do seu harware?", "Atenção", JOptionPane.YES_NO_OPTION) != 0) {
								JOptionPane.showMessageDialog(this, "Operação cancelada", "Finalizando operação", 1);
								return;
							}
								
						}
						NumberFormat nf = NumberFormat.getIntegerInstance();
						info.setText("Calculando primos até " + nf.format(maxPrimo));
						ponte = new PonteSincronizada();
						Thread produtor = new Thread(new Produtor(ponte, maxPrimo));
						Thread consumidor = new Thread(new Consumidor(ponte, this));
						produtor.setPriority(Thread.MAX_PRIORITY);
						consumidor.setPriority(Thread.MIN_PRIORITY);
						produtor.start();
						consumidor.start();
					} else {
						panel2.setVisible(false);
						panel1.setVisible(true);
						texto.setText("Não há nenhum número primo positivo.");
					}
				} catch(Exception ex) {
					panel2.setVisible(false);
					panel1.setVisible(true);
					JOptionPane.showMessageDialog(this, "Número inserido inválido.\nPor favor, insira um número válido", "Erro", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if(e.getSource() == cancelarOperacao) {
			try {
				ponte.finalizar();
			} catch (InterruptedException e1) {
				JOptionPane.showMessageDialog(this, "Não foi possivel realizar o cálculo", "Erro", JOptionPane.ERROR_MESSAGE);
			}
			panel1.setVisible(true);
		} else if(e.getSource() == abrir) {
			JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter extencao = new FileNameExtensionFilter("Primos", "primo");
			fc.setFileFilter(extencao);
			fc.setAcceptAllFileFilterUsed(false);
			fc.setMultiSelectionEnabled(false);
			int returnVal = fc.showOpenDialog(this); //showSaveDialog é pra salvar
			File f = fc.getSelectedFile();
			texto.append("Abrindo: " + f.getName() + ".\n");
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				String path = f.getAbsolutePath();
				Path p = Paths.get(path);
				
				try {
					String retorno = new String(Files.readAllBytes(p));
					JOptionPane.showMessageDialog(this, "Arquivo carregado com sucesso!", "Sucesso", JOptionPane.PLAIN_MESSAGE);
					texto.setText(retorno);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(this, "Infelizmente não foi possível abrir o seu arquivo", "Sucesso", JOptionPane.PLAIN_MESSAGE);
				}
			}
		} else if(e.getSource() == salvar) {
			
			JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter extencao = new FileNameExtensionFilter("Primos", "primo");
			fc.setFileFilter(extencao);
			fc.setAcceptAllFileFilterUsed(false);
			fc.setMultiSelectionEnabled(false);
			int returnVal = fc.showSaveDialog(this); //showSaveDialog é pra salvar
			File file = fc.getSelectedFile();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                	String p = file.getAbsolutePath();
                	Path path = Paths.get(p.endsWith(".primo") ? p : p + ".primo");
                	Files.write(path, texto.getText().getBytes());
                	
                } catch(IOException e2) {
                	JOptionPane.showMessageDialog(this, "Desculpa, não foi possível salvar seu arquivo", "Sucesso", JOptionPane.PLAIN_MESSAGE);
                }
                
                JOptionPane.showMessageDialog(this, "Arquivo salvo com sucesso!", "Sucesso", JOptionPane.PLAIN_MESSAGE);
                
            } else {
            	JOptionPane.showMessageDialog(this, "Comando de salvar cancelado pelo usuário.\n", "Cancelado", JOptionPane.PLAIN_MESSAGE);
            }
		} else {
			antesDeFechar();
		}
		
	}
	
	public static void inicializa() {
		String nome;
		do {
			nome = JOptionPane.showInputDialog(null, "Insira seu login", "Bem vindo(a)", 1);
			if(nome == null || nome.equals(""))
				JOptionPane.showMessageDialog(null, "Este nome não pode ser aceito em nosso sistema.\nPor favor insira um nome coerente, ou digite \"SAIR\"\n(sem aspas, podendo ser maiusculo ou minusculo)", "Atenção", JOptionPane.ERROR_MESSAGE);
		} while(nome == null || nome.equals(""));
		if(!nome.equalsIgnoreCase("sair"))
			new Janela1(nome);
	}
	
	public void antesDeFechar() {
		if(JOptionPane.showConfirmDialog(this, "Deseja realmente sair?", "Atenção", JOptionPane.YES_NO_OPTION) == 0)
			System.exit(1);
		else {
			JOptionPane.showMessageDialog(this, "Vamos continuar então!", "Heba", 1);
		}
	}


	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosing(WindowEvent e) {
		antesDeFechar();
		
	}


	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}

class Produtor implements Runnable {
	private Ponte ponte;
	private final long maxPrimo;
	
	public Produtor(Ponte ponte, long maxPrimo) {
		this.ponte = ponte;
		this.maxPrimo = maxPrimo;
	}

	@Override
	public void run() {
		
		List<Long> primos = new ArrayList<>();
		primos.add(2L);
		primos.add(3L);
		try {
			if(maxPrimo > 1) {
				ponte.set(2L);
				if(maxPrimo > 2) {
					ponte.set(3L);
					
					A:
					for(long i = 5; i <= maxPrimo; i += 2) {
						for (int i2 = 1; primos.get(i2) * primos.get(i2) <= i; i2 ++) {
							if(i % primos.get(i2) == 0) {
								continue A;
							}
						}
						
						if(ponte.isContinuar()) {
							primos.add(i);
							ponte.set(i);
						} else {
							break;
						}
					}
					
					ponte.finalizar();
					//ponte.setVezes(primos.size());
				}
			}
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, "Não foi possivel realizar o cálculo", "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}
}

class Consumidor implements Runnable {

	private Ponte ponte;
	private Janela1 janela;
	
	public Consumidor(Ponte ponte, Janela1 janela) {
		this.ponte = ponte;
		this.janela = janela;
	}
	
	
	
	@Override
	public void run() {
		
			janela.getTexto().setText("único número primo par: ");
			long count = 0;
		while(ponte.isContinuar() || ponte.temNaPonte()) {
			
			try {
				List<Long> lista = ponte.get();{
					for (Long primo : lista) {
						janela.getTexto().append(primo + (count % 20 != 0 ? " " : "\n"));
						janela.getTexto().setCaretPosition(janela.getTexto().getDocument().getLength());
						++count;
					}
				}
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(null, "Não foi possivel realizar o cálculo", "Erro", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		janela.getPanel2().setVisible(false);
		janela.getPanel1().setVisible(true);
	}
	
}
