package io.bootify.task_share.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.Gson;
import io.bootify.task_share.utils.Util;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name="tarefa")

@Setter
@Getter
@Builder(toBuilder = true)

@AllArgsConstructor
@NoArgsConstructor
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String descricao;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(nullable = true)
    private LocalDateTime dataHoraCriacao;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "dd/MM/yyyy")
    @Column(nullable = true)
    private LocalDate dataInicioTarefa;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Column(nullable = true)
    private LocalTime horaInicioTarefa;

    @Column(nullable = false)
    private boolean alertar;

    @Column(nullable = false)
    private boolean finalizada;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(nullable = true)
    private LocalDateTime dataHoraFinalizacao;

    @ElementCollection
    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Meta> metas;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    private Usuario usuario;

    @ElementCollection
    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membro> membros;

    @SneakyThrows
    @Override
    public String toString() {

        Map<String, String> mapTarefa = new HashMap<>();

        Gson gson = new Gson();

        mapTarefa.put("id", Util.md5(String.valueOf(id)));
        mapTarefa.put("titulo", titulo);
        mapTarefa.put("descricao", descricao);
        mapTarefa.put("dataHoraCriacao", String.valueOf(dataHoraCriacao));
        mapTarefa.put("dataInicioTarefa", String.valueOf(dataInicioTarefa));
        mapTarefa.put("horaInicioTarefa", String.valueOf(horaInicioTarefa));
        mapTarefa.put("alertar", String.valueOf(alertar));
        mapTarefa.put("finalizada", String.valueOf(finalizada));
        mapTarefa.put("dataHoraFinalizacao", String.valueOf(dataHoraFinalizacao));
        mapTarefa.put("progress", String.valueOf(metas.stream().filter(Meta::isAlcancada).toList().size()));
        mapTarefa.put("total_tarefas", String.valueOf(metas.size()));
        mapTarefa.put("total_concluidas", String.valueOf(metas.stream().filter(Meta::isAlcancada).toList().size()));
        mapTarefa.put("total_membros", String.valueOf(membros.size()));

        return gson.toJson(mapTarefa);
    }

    public List<Tarefa> ordenaTarefas(List<Tarefa> tarefas) {
        List<Tarefa> resTarefasOrdenadas = new LinkedList<>();
        tarefas.stream()
                .sorted((p1, p2) -> p2.getDataHoraCriacao().compareTo(p1.getDataHoraCriacao()))
                .forEach(lj -> resTarefasOrdenadas.add(lj));
        return resTarefasOrdenadas;
    }
}
