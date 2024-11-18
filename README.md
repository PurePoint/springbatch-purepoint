# springbatch-purepoint

**테스트 방법**

src/test/com/purepoint/youtubebatch/BatchTest 의 testScheduleJob을 실행시켜주면 된다.

*주의*

유튜브 할당량 제한이 있어 step1 또는 step2 하나만 테스트해보기를 권장.
src/main/java/com/purepoint/youtubebatch/BatchConfiguration 의 youtubeApiJob 부분에서 step2 주석처리 후 실행.

```
@Bean  
public Job youtubeApiJob(JobRepository jobRepository, @Qualifier("youtubeVideoStep") Step step1,  
                         @Qualifier("youtubePlaylistStep") Step step2,JobCompletionNotificationListener listener) {  
    return new JobBuilder("youtubeApiJob", jobRepository)  
            .listener(listener)  
            .start(step1)  
            //.next(step2)  
            .build();  
}
```