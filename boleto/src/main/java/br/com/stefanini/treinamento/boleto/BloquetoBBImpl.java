package br.com.stefanini.treinamento.boleto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import br.com.stefanini.treinamento.exception.ManagerException;

public abstract class BloquetoBBImpl implements BloquetoBB {

	protected String codigoBanco;
	protected String codigoMoeda;
	protected String fatorVencimento;
	protected Date dataVencimento;
	protected Date dataBase;
	protected BigDecimal valor;
	protected String numeroConvenioBanco;
	protected String complementoNumeroConvenioBancoSemDV;
	protected String numeroAgenciaRelacionamento;
	protected String contaCorrenteRelacionamentoSemDV;
	protected String tipoCarteira;

	private int dvCodigoBarras;

	protected abstract void validaDados() throws ManagerException;

	/**
	 * Inicializa o fator de vencimento
	 */
	protected void setFatorVencimento() {

		long dias = diferencaEmDias(dataBase, dataVencimento);

		// TODO: EXPLICAR O QUE ESTE MÉTODO ESTÁ FAZENDO

		fatorVencimento = String.format("%04d", dias);

	}

	/**
	 * Inicializa os valores, formata
	 */
	protected void init() {

		setFatorVencimento();

	}

	/**
	 * Retorna o valor formatado do boleto bancário
	 * 
	 * @return
	 */
	protected String getValorFormatado() {

		// TODO: Explicar o que este método está fazendo
		/*
		 * Retorna uma String com dez caracteres. Sendo o numero decimal
		 * convertido em String (ou seja, sem a virgula) acrescido de zeros à
		 * esquerda até o total de dez digitos.
		 */
		return String.format(
				"%010d",
				Long.valueOf(valor.setScale(2, RoundingMode.HALF_UP).toString()
						.replace(".", "")));
	}

	/**
	 * Formata o número do convênio da Linha Digitável
	 * 
	 * @return
	 */
	protected abstract String getLDNumeroConvenio();

	/**
	 * Retorna o código de barras do Bloqueto
	 * 
	 * @return código de barras
	 */
	protected abstract String getCodigoBarrasSemDigito();

	public abstract String getCodigoBarras();

	/**
	 * Campo 5 da Linha Digitável
	 * 
	 * @return
	 */
	private String ldCampo5() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(fatorVencimento);
		buffer.append(getValorFormatado());
		return buffer.toString();
	}

	/**
	 * Campo 4 da Linha Digitável
	 * 
	 * @return
	 */
	private String ldCampo4() {
		return String
				.valueOf(digitoVerificadorCodigoBarras(getCodigoBarrasSemDigito()));
	}

	/**
	 * Campo 3 da Linha Digitável
	 * 
	 * @return
	 */
	private String ldCampo3() {
		return String.format("%s.%s", getCodigoBarras().substring(34, 39),
				getCodigoBarras().substring(39, 44));
	}

	/**
	 * Campo 2 da Linha Digitável
	 * 
	 * @return
	 */
	private String ldCampo2() {
		return String.format("%s.%s", getCodigoBarras().substring(24, 29),
				getCodigoBarras().substring(29, 34));
	}

	/**
	 * Calcula o digito verificador do campo
	 * 
	 * @param campo
	 * @return
	 */
	protected int digitoVerificadorPorCampo(String campo, boolean valor) {
		// TODO: COMPLETAR
		String campo1 = campo.replace(".", "");
		char[] array = campo1.toCharArray();
		Integer n1 = 0;
		int n2 = 0;
		int n3 = 0;
		int n4 = 0;
		int soma = 0;
		int mult = 2;
		int result = 0;
		int post = 0;
		String ex = null;
		for (int i = array.length - 1; i >= 0; i--) {
			ex = String.valueOf(array[i]);
			n1 = Integer.parseInt(ex);
			n1 = n1 * mult;
			if (mult == 2) {
				mult = 1;
			} else {
				mult = 2;
			}
			if (n1 > 9) {
				n2 = n1 % 10;
				n4 = n1 / 10;
				n3 = n4 % 10;
				n1 = n2 + n3;
			}
			soma = soma + n1;
		}
		post = soma / 10;
		post = (post * 10) + 10;
		result = post - soma;
		if (result == 10) {
			result = 0;
		}
		return result;
	}

	/**
	 * Calcula o digito verificado do código de barras
	 * 
	 * @param codigoBarras
	 * @return
	 */
	protected int digitoVerificadorCodigoBarras(String codigoBarras) {
		// TODO: COMPLETAR
		int mult = 2;
		char[] array = codigoBarras.toCharArray();
		int n1 = 0;
		int n2 = 0;
		int soma = 0;
		int result = 0;
		String ex = null;
		for (int i = array.length - 1; i >= 0; i--) {
			ex = String.valueOf(array[i]);
			n1 = Integer.parseInt(ex);
			n1 = n1 * mult;
			if (mult + 1 == 10) {
				mult = 2;
			} else {
				mult++;
			}
			soma = soma + n1;
		}
		n2 = soma % 11;
		result = 11 - n2;
		if ((result == 0) || (result == 10) || (result == 11)) {
			result = 1;
		}
		return result;
	}

	/**
	 * Campo 1 da Linha Digitável
	 * 
	 * - Código do Banco - Código da Moeda - Número do convênio
	 * 
	 * @return
	 */
	private String ldCampo1() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(codigoBanco);
		buffer.append(codigoMoeda);
		buffer.append(getLDNumeroConvenio());
		return buffer.toString();

	}

	public String getLinhaDigitavel() {

		init();

		StringBuilder buffer = new StringBuilder();
		buffer.append(ldCampo1());
		buffer.append(digitoVerificadorPorCampo(ldCampo1(), true));
		buffer.append(" ");
		buffer.append(ldCampo2());
		buffer.append(digitoVerificadorPorCampo(ldCampo2(), false));
		buffer.append(" ");
		buffer.append(ldCampo3());
		buffer.append(digitoVerificadorPorCampo(ldCampo3(), false));
		buffer.append(" ");
		buffer.append(ldCampo4());
		buffer.append(" ");
		buffer.append(ldCampo5());

		return buffer.toString();
	}

	/**
	 * Retorna a diferença em dias de duas datas
	 * 
	 * @param dataInicial
	 *            Data inicial
	 * @param dataFinal
	 *            Data final
	 * @return
	 */
	protected static long diferencaEmDias(Date dataInicial, Date dataFinal) {

		// TODO: Estude a Math e escreva aqui o que este método está fazendo

		/*
		 * Retorna a diferença de dias entre a data final e data inicial. O
		 * metodo getTime retorna a data em milisegundos. Como 86400000 é igual
		 * a um dia, a divisão dará a diferença das datas em dias.
		 */
		return Math
				.round((dataFinal.getTime() - dataInicial.getTime()) / 86400000D);
	}

	public int getDvCodigoBarras() {

		getCodigoBarras();

		return dvCodigoBarras;
	}
}
