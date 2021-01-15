Задача: сделать класс с методами int save(byte[] buffer); и byte[] get(int index);. При этом сохранять и забирать могут много потоков и с разной нагрузкой. Нужно предусмотреть сохранение на диск, чтобы после перезагрузки данные не терялись. требуется предусмотреть автотест своего решения

JUnit test'ы - ru.navilab.chessdevtest.ChessDevJUnitTest

Алгоритм - 
т.к. не было сказано, что использовать фреймворки за пределами JVM, то пришлось изобретать велосипед.
Сохранение данных выполняется в файлы - ru.navilab.chessdevtest.impl.FileSaver
всего два файла: storage_n.index и storage_n.data (n - пока 0)
Т.к. один файл сразу ограничивает Multi Threading (MT) необходимостью синхронизации, то предусмотрена возможность 
расширения - реализации пула FileSaver'ов для одновременной записи в разных тредах. Пока для простоты (лимит времени 
я сам себе такой сделал) сделан фейк пул из одного FileSaver'а.

Основной интерфейс задачи - ru.navilab.chessdevtest.ChessDevTest
Реализация - ChessDevTestImpl. В ней есть Indexer - для получения index'ов. PersistLayer - для сохранения куда либо (реализация - в файлы) и
CacheLayer - реализация кэша на хэштейбле и timestampaми. Также в ChessDevTestImpl есть фабричные методы создания экземпляра класса и 
дополнительные методы по инициализации (загрузка данных, если программа была завершена) и закрытию записи в файл(ы).

PersistLayer, CacheLayer можно реализовать по другому (Redis например) - достаточно будет фабричный метод с кастомными Layer'ами вызвать.

Допущения - 
1) тот самый OneFileSaver на самом деле не "Pool".
2) IndexAndPositionList предполагает, что оценочно максимум 128 мб в пуле памяти потратить на него - не так уж и много.

Treshold'ы по очистке мемори кэша надо настраивать в SimpleMapCacheLayer.

Проверял - JUnit' тестами (см. javadocs)