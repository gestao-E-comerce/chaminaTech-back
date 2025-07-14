package chaminaTech.Repository.Graficos;

import chaminaTech.Entity.Relatorio;
import chaminaTech.Graficos.GraficoProduto.ProdutoMaisVendido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatorioProdutoRepository extends JpaRepository<Relatorio, Long> {

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoProduto.ProdutoMaisVendido(
                    pv.produto.nome,
                    COUNT(DISTINCT v.id),
                    SUM(COALESCE(pv.quantidade, 0)),
                    SUM(COALESCE(pv.quantidade, 0) * COALESCE(pv.produto.valor, 0))
                )
                FROM ProdutoVenda pv
                LEFT JOIN pv.venda v
                WHERE v.matriz.id = :matrizId
                AND v.consumoInterno = false
                AND v.deletado = false
                AND pv.ativo = true
                AND (:deletado IS NULL OR pv.produto.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:produtoId IS NULL OR pv.produto.id = :produtoId)
                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                GROUP BY pv.produto.nome
                ORDER BY SUM(COALESCE(pv.quantidade, 0)) ASC
            """)
    List<ProdutoMaisVendido> gerarProdutosMaisVendidosOrdenadoPorQuantidadeAsc(
            @Param("matrizId") Long matrizId,
            @Param("funcionarioId") Long funcionarioId,
            @Param("produtoId") Long produtoId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("deletado") Boolean deletado,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoProduto.ProdutoMaisVendido(
                    pv.produto.nome,
                    COUNT(DISTINCT v.id),
                    SUM(COALESCE(pv.quantidade, 0)),
                    SUM(COALESCE(pv.quantidade, 0) * COALESCE(pv.produto.valor, 0))
                )
                FROM ProdutoVenda pv
                LEFT JOIN pv.venda v
                WHERE v.matriz.id = :matrizId
                AND v.consumoInterno = false
                AND v.deletado = false
                AND pv.ativo = true
                AND (:deletado IS NULL OR pv.produto.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:produtoId IS NULL OR pv.produto.id = :produtoId)
                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                GROUP BY pv.produto.nome
                ORDER BY SUM(COALESCE(pv.quantidade, 0)) DESC
            """)
    List<ProdutoMaisVendido> gerarProdutosMaisVendidosOrdenadoPorQuantidadeDesc(
            @Param("matrizId") Long matrizId,
            @Param("funcionarioId") Long funcionarioId,
            @Param("produtoId") Long produtoId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("deletado") Boolean deletado,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoProduto.ProdutoMaisVendido(
                    pv.produto.nome,
                    COUNT(DISTINCT v.id),
                    SUM(COALESCE(pv.quantidade, 0)),
                    SUM(COALESCE(pv.quantidade, 0) * COALESCE(pv.produto.valor, 0))
                )
                FROM ProdutoVenda pv
                LEFT JOIN pv.venda v
                WHERE v.matriz.id = :matrizId
                AND v.consumoInterno = false
                AND v.deletado = false
                AND pv.ativo = true
                AND (:deletado IS NULL OR pv.produto.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:produtoId IS NULL OR pv.produto.id = :produtoId)
                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                GROUP BY pv.produto.nome
                ORDER BY SUM(COALESCE(pv.quantidade, 0) * COALESCE(pv.produto.valor, 0)) ASC
            """)
    List<ProdutoMaisVendido> gerarProdutosMaisVendidosOrdenadoPorValorAsc(
            @Param("matrizId") Long matrizId,
            @Param("funcionarioId") Long funcionarioId,
            @Param("produtoId") Long produtoId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("deletado") Boolean deletado,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoProduto.ProdutoMaisVendido(
                    pv.produto.nome,
                    COUNT(DISTINCT v.id),
                    SUM(COALESCE(pv.quantidade, 0)),
                    SUM(COALESCE(pv.quantidade, 0) * COALESCE(pv.produto.valor, 0))
                )
                FROM ProdutoVenda pv
                LEFT JOIN pv.venda v
                WHERE v.matriz.id = :matrizId
                AND v.consumoInterno = false
                AND v.deletado = false
                AND pv.ativo = true
                AND (:deletado IS NULL OR pv.produto.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:produtoId IS NULL OR pv.produto.id = :produtoId)
                AND (
                    (:balcao = true AND v.balcao = true) OR
                    (:retirada = true AND v.retirada = true) OR
                    (:entrega = true AND v.entrega = true) OR
                    (:mesa = true AND v.mesa IS NOT NULL) OR
                    (:balcao IS NULL AND :retirada IS NULL AND :entrega IS NULL AND :mesa IS NULL)
                )
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                GROUP BY pv.produto.nome
                ORDER BY SUM(COALESCE(pv.quantidade, 0) * COALESCE(pv.produto.valor, 0)) DESC
            """)
    List<ProdutoMaisVendido> gerarProdutosMaisVendidosOrdenadoPorValorDesc(
            @Param("matrizId") Long matrizId,
            @Param("funcionarioId") Long funcionarioId,
            @Param("produtoId") Long produtoId,
            @Param("balcao") Boolean balcao,
            @Param("retirada") Boolean retirada,
            @Param("entrega") Boolean entrega,
            @Param("mesa") Boolean mesa,
            @Param("deletado") Boolean deletado,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoProduto.ProdutoMaisVendido(
                    pv.produto.nome,
                    COUNT(DISTINCT v.id),
                    SUM(COALESCE(pv.quantidade, 0)),
                    SUM(COALESCE(pv.quantidade, 0) * COALESCE(pv.produto.valor, 0))
                )
                FROM ProdutoVenda pv
                LEFT JOIN pv.venda v
                WHERE v.matriz.id = :matrizId
                AND v.consumoInterno = false
                AND v.deletado = false
                AND pv.ativo = true
                AND v.retirada = true
                AND (:deletado IS NULL OR pv.produto.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:produtoId IS NULL OR pv.produto.id = :produtoId)
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                GROUP BY pv.produto.nome
                ORDER BY SUM(COALESCE(pv.quantidade, 0)) DESC
            """)
    List<ProdutoMaisVendido> gerarGraficoProdutoMaisVendidoRetirada(
            @Param("matrizId") Long matrizId,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("produtoId") Long produtoId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoProduto.ProdutoMaisVendido(
                    pv.produto.nome,
                    COUNT(DISTINCT v.id),
                    SUM(COALESCE(pv.quantidade, 0)),
                    SUM(COALESCE(pv.quantidade, 0) * COALESCE(pv.produto.valor, 0))
                )
                FROM ProdutoVenda pv
                LEFT JOIN pv.venda v
                WHERE v.matriz.id = :matrizId
                AND v.consumoInterno = false
                AND v.deletado = false
                AND pv.ativo = true
                AND v.balcao = true
                AND (:deletado IS NULL OR pv.produto.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:produtoId IS NULL OR pv.produto.id = :produtoId)
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                GROUP BY pv.produto.nome
                ORDER BY SUM(COALESCE(pv.quantidade, 0)) DESC
            """)
    List<ProdutoMaisVendido> gerarGraficoProdutoMaisVendidoBalcao(
            @Param("matrizId") Long matrizId,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("produtoId") Long produtoId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoProduto.ProdutoMaisVendido(
                    pv.produto.nome,
                    COUNT(DISTINCT v.id),
                    SUM(COALESCE(pv.quantidade, 0)),
                    SUM(COALESCE(pv.quantidade, 0) * COALESCE(pv.produto.valor, 0))
                )
                FROM ProdutoVenda pv
                LEFT JOIN pv.venda v
                WHERE v.matriz.id = :matrizId
                AND v.consumoInterno = false
                AND v.deletado = false
                AND pv.ativo = true
                AND v.entrega = true
                AND (:deletado IS NULL OR pv.produto.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:produtoId IS NULL OR pv.produto.id = :produtoId)
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                GROUP BY pv.produto.nome
                ORDER BY SUM(COALESCE(pv.quantidade, 0)) DESC
            """)
    List<ProdutoMaisVendido> gerarGraficoProdutoMaisVendidoEntrega(
            @Param("matrizId") Long matrizId,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("produtoId") Long produtoId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
                SELECT new chaminaTech.Graficos.GraficoProduto.ProdutoMaisVendido(
                    pv.produto.nome,
                    COUNT(DISTINCT v.id),
                    SUM(COALESCE(pv.quantidade, 0)),
                    SUM(COALESCE(pv.quantidade, 0) * COALESCE(pv.produto.valor, 0))
                )
                FROM ProdutoVenda pv
                LEFT JOIN pv.venda v
                WHERE v.matriz.id = :matrizId
                AND v.consumoInterno = false
                AND v.deletado = false
                AND pv.ativo = true
                AND v.mesa IS NOT NULL
                AND (:deletado IS NULL OR pv.produto.deletado = :deletado)
                AND (:funcionarioId IS NULL OR v.funcionario.id = :funcionarioId)
                AND (:produtoId IS NULL OR pv.produto.id = :produtoId)
                AND v.dataVenda >= COALESCE(:dataInicio, v.dataVenda)
                AND v.dataVenda <= COALESCE(:dataFim, v.dataVenda)
                GROUP BY pv.produto.nome
                ORDER BY SUM(COALESCE(pv.quantidade, 0)) DESC
            """)
    List<ProdutoMaisVendido> gerarGraficoProdutoMaisVendidoMesa(
            @Param("matrizId") Long matrizId,
            @Param("deletado") Boolean deletado,
            @Param("funcionarioId") Long funcionarioId,
            @Param("produtoId") Long produtoId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}