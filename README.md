# scheduler2 0.0.1

O **scheduler2** é um *bitcode* de agendamento para o [thrustjs](https://github.com/Thrustjs/thrust).

Por causa da nova versão do *thrust* que trabalha com o [GraalVM](http://graalvm.org); o **scheduler2** trabalha com configuração de *script* a ser agendado; logo, cada *script* é executado em um contexto JavaScript separado de sua aplicação principal.

## Instalação

Utilize o *tpm* para instalar este *bitcode*:

```sh
tpm install scheduler2
```

## Configuração do agendador

Inicie o **scheduler2** informando a quantidade de suas *treads*. A biblioteca contém a função `.initScheduler()`; que deve ser chamada no máximo uma vez em seu *código*.

Por exemplo:

```js
const scheduler = require('scheduler2')
scheduler.initScheduler()
```

Neste exemplo, o *pool* de threads está sendo iniciado com **4** threads. E aqui:

```js
const scheduler = require('scheduler2')
scheduler.initScheduler(1,5)
```

Nós estamos iniciando o agendador com no mínimo **1** thread, e que no máximo o *pool* irá crescer com até **5** threads.

## Agendando

O agendador possui a seguinte função de agendamento:

```js
schedule(taskScript, time, now)
```

O primeiro parâmetro `taskScript` identifica o *script* que irá executar a tarefa a ser agendada. Seu caminho pode ser absoluto ou relativo ao diretório raiz da aplicação.

O segundo parâmetro `time` refere-se ao tempo em que o *script* `taskScript` será executado periodicamente.
E o terceiro parâmetro, identificar se o *script* deverá ser executado de imediato; *antes* de o tempo previsto ocorrer.

Lembre-se que cada agendamento é executado pelo **scheduler2** em um contexto/thread separado.

### Agendamento por período

Se para o parâmetro `time`, você informar um valor numérico inteiro; então, o agendador entende que estamos agendando uma tarefa para ser executada continuamente a cada **período** de milissegundos.

Segue um exemplo:

```js
const scheduler = require('scheduler2')
scheduler.schedule('src/tasks/task01.js', 360000)
```

Acima, estamos informando ao agendador que a tarefa contida no *script* "`src/tasks/task01.js`" estará sendo executada a cada 6 minutos (360000 milissegundos). E neste exemplo:

```js
const scheduler = require('scheduler2')
scheduler.schedule('src/tasks/task01.js', 360000, true)
```

Temos a mesma configuração e com a diferença: o parâmetro `now` está definido como `true`. Isto significa que a tarefa do *script* será executada imediatamnente; e depois o agendador irá executá-la  a cada 6 minutos.

### Agendamento por hora

Caso o parâmetro `time` for um *array* de strings; neste caso, o agendador entenderá que a tarefa deverá ser executada periodicamente na **hora** que foi informada.

Vamos para um exemplo para entender a várias possibilidades e seus significados:

```js
const scheduler = require('scheduler2')
// Agendamento 01
scheduler.schedule('src/tasks/task02.js', ['8'])
// Agendamento 02
scheduler.schedule('src/tasks/task03.js', ['9:15'])
// Agendamento 03
scheduler.schedule('src/tasks/task04.js', ['10:20:45'])
// Agendamento 04
scheduler.schedule('src/tasks/task05.js', ['9:20', '14:33'])
```

Estamos informando para o agendador, o seguinte:

- O agendamento 01: Todo dia, às 08:00, o agendador irá executar a tarefa do *script* `scr/tasks/task02.js`.
- O agendamento 02: Todo dia, às 09:15, o agendador irá executar a tarefa do *script* `scr/tasks/task03.js`.
- O agendamento 03: Todo dia, às 10:20:45, o agendador irá executar a tarefa do *script* `scr/tasks/task04.js`.
- O agendamento 04: Todo dia, às 09:20 e depois às 14:33, o agendador irá executar a tarefa do *script* `scr/tasks/task05.js`.

## O *script* da tarefa

O arquivo JavaScript que contém a tarefa deverá exportar uma função diretamente, ou uma função com o nome `task`.

Por exemplo, se agendarmos:

```js
const scheduler = require('scheduler2')
scheduler.schedule('src/tasks/task07.js', ['23:59:59'], true)
```

Então, o arquivo `task07` deve exportar uma função assim:

```js
// Arquivo task07.js ...
exports = (schedulerTask) => {
    console.log('Faça algo durante a sua execução')
}
```

ou, assim:

```js
// Arquivo task07.js ...
exports = {
    task: (schedulerTask) => {
        console.log('Faço alguma coisa por você ;-)')
    }
}
```

O parâmetro de entrada `schedulerTask` contém o método/função `cancel()`, que é utlizado para cancelar a *continuidade* do agendador de executar a tarefa.

Veja o exemplo [task02.js](src/test/thrust/task02.js):

```js
const resource = {
    count: 5
}
exports = (schedulerTask) => {
  console.log(`Task 02 - Count: ${resource.count}`)
  --resource.count
  if (!resource.count) {
    schedulerTask.cancel()
  }
}
```

Aqui vemos um caso em que uma tarefa é executada umas 5 vezes; e depois disto, informamos ao agendador que a
tarefa poderá ser removida do agendador, por chamarmos a função `schedulerTask.cancel()`). Logo, a tarefa deste *script* não será mais executada pelo agendador; podendo outra tarefa ser agendada em seu lugar.
