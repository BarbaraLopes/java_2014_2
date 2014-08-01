/*
 * Implementação do Bloqueto de Cobranças do Banco do Brasil
 * - Convênio com 4 posições 
 * 
 * scoelho@stefanini.com
 */
package br.com.stefanini.treinamento.boleto;

import java.math.BigDecimal;
import java.util.Date;

import br.com.stefanini.treinamento.exception.ManagerException;

public class BloquetoBBConvenio4 extends BloquetoBBImpl implements BloquetoBB {

	@Override
	protected void validaDados() throws ManagerException {

		if (codigoBanco == null || codigoBanco.length() != 3) {
			throw new ManagerException(
					"Código do Banco não informado ou com tamanho diferente de 3 posições");
		}

		if (codigoMoeda == null || codigoMoeda.length() != 1) {
			throw new ManagerException(
					"Código de moeda não informado ou inválido");
		}

		if (dataVencimento == null) {
			throw new ManagerException("Data de vencimento não informada");
		}

		if (valor == null) {
			throw new ManagerException(
					"Valor do bloqueto bancÃ¡rio não informado");
		}

		if (numeroConvenioBanco == null || numeroConvenioBanco.length() != 4) {
			throw new ManagerException(
					"número de convênio não informado ou o convênio informado é inválido. O convênio deve ter 4 posições");
		}

		if (complementoNumeroConvenioBancoSemDV == null
				&& complementoNumeroConvenioBancoSemDV.length() != 7) {
			throw new ManagerException(
					"Complemento do número do convênio não informado. O complemento deve ter 7 posições");
		}

		if (numeroAgenciaRelacionamento == null
				|| numeroAgenciaRelacionamento.length() != 4) {
			throw new ManagerException(
					"número da agência de Relacionamento não informado. O número da agência deve ter 4 posições");
		}

		if (contaCorrenteRelacionamentoSemDV == null
				|| contaCorrenteRelacionamentoSemDV.length() != 8) {
			throw new ManagerException(
					"Conta corrente de relacionamento não informada. O número da conta deve ter 8 posições");
		}

		if (tipoCarteira == null || tipoCarteira.length() != 2) {
			throw new ManagerException(
					"Tipo carteira não informado ou o valor é inválido");
		}

		if (dataBase == null) {
			throw new ManagerException("A database não foi informada.");
		}

	}

	public BloquetoBBConvenio4(String codigoBanco, String codigoMoeda,
			Date dataVencimento, Date dataBase, BigDecimal valor,
			String numeroConvenioBanco,
			String complementoNumeroConvenioBancoSemDV,
			String numeroAgenciaRelacionamento,
			String contaCorrenteRelacionamentoSemDV, String tipoCarteira)
			throws ManagerException {

		// TODO: INICIALIZAR DADOS
		this.codigoBanco = codigoBanco;
		this.codigoMoeda = codigoMoeda;
		this.dataVencimento = dataVencimento;
		this.valor = valor;
		this.numeroConvenioBanco = numeroConvenioBanco;
		this.complementoNumeroConvenioBancoSemDV = complementoNumeroConvenioBancoSemDV;
		this.numeroAgenciaRelacionamento = numeroAgenciaRelacionamento;
		this.contaCorrenteRelacionamentoSemDV = contaCorrenteRelacionamentoSemDV;
		this.tipoCarteira = tipoCarteira;
		this.dataBase = dataBase;
		validaDados();

	}

	@Override
	protected String getLDNumeroConvenio() {
		String convenio = String.format("%04d",
				Long.valueOf(numeroConvenioBanco));
		return String.format("%s.%s", convenio.substring(0, 1),
				convenio.substring(1, 5));

	}

	// TODO: @sandro - refatorar os métodos getCodigoBarrasSemDigito() e
	// getCodigoBarras()

	@Override
	protected String getCodigoBarrasSemDigito() {

		init();

		StringBuilder buffer = new StringBuilder();
		buffer.append(codigoBanco);
		buffer.append(codigoMoeda);
		buffer.append(fatorVencimento);
		buffer.append(getValorFormatado());
		buffer.append(numeroConvenioBanco);
		buffer.append(complementoNumeroConvenioBancoSemDV);
		buffer.append(numeroAgenciaRelacionamento);
		buffer.append(contaCorrenteRelacionamentoSemDV);
		buffer.append(tipoCarteira);
		// TODO: COMPLETAR

		return buffer.toString();
	}

	@Override
	public String getCodigoBarras() {

		init();

		StringBuilder buffer = new StringBuilder();

		buffer.append(codigoBanco); // Campo 01-03 (03)
		buffer.append(codigoMoeda); // Campo 04-04 (01)
		buffer.append(digitoVerificadorCodigoBarras(getCodigoBarrasSemDigito()));
		buffer.append(fatorVencimento); // Campo 06-09 (04)
		buffer.append(getValorFormatado()); // Campo 10-19 (10)
		buffer.append(numeroConvenioBanco); // Campo 20-23 (04)
		buffer.append(complementoNumeroConvenioBancoSemDV); // Campo 24-30 (07)
		buffer.append(numeroAgenciaRelacionamento); // Campo 31-34 (04)
		buffer.append(contaCorrenteRelacionamentoSemDV);
		buffer.append(tipoCarteira);

		// TODO: COMPLETAR

		return buffer.toString();
	}

}
