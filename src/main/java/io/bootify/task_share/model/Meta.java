package io.bootify.task_share.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.Gson;
import io.bootify.task_share.utils.Util;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="meta")

@Setter
@Getter
@Builder(toBuilder = true)

@AllArgsConstructor
@NoArgsConstructor
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String descricao;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(nullable = true)
    private LocalDateTime dataHoraCriacao;

    @Column(nullable = false)
    private boolean alcancada;

    @Column(nullable = false)
    private int prioridade;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(nullable = true)
    private LocalDateTime dataHoraAlcanceMeta;

    @ManyToOne
    @JoinColumn(name = "tarefa_id", nullable = false)
    @JsonBackReference
    private Tarefa tarefa;

    public List<Meta> ordenaMetas(List<Meta> metas) {
        List<Meta> resMetasOrdenadas = new LinkedList<>();
        metas.stream()
                .sorted(Comparator.comparingInt(Meta::getPrioridade)) // Ordenação decrescente por prioridade
                .forEach(lj -> resMetasOrdenadas.add(lj));
        return resMetasOrdenadas;
    }

    @SneakyThrows
    @Override
    public String toString() {

        Map<String, String> mapMetaTarefa = new HashMap<>();

        Gson gson = new Gson();

        mapMetaTarefa.put("id", Util.md5(String.valueOf(id)));
        mapMetaTarefa.put("descricao", descricao);
        mapMetaTarefa.put("dataHoraCriacao", String.valueOf(dataHoraCriacao));
        mapMetaTarefa.put("alcancada", String.valueOf(alcancada));
        mapMetaTarefa.put("dataHoraAlcanceMeta", String.valueOf(dataHoraAlcanceMeta));
        mapMetaTarefa.put("prioridade", String.valueOf(prioridade));

        return gson.toJson(mapMetaTarefa);
    }
}
