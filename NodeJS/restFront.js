async function getUser() {// 로딩 시 사용자 가져오는 함수
    try {
      const res = await axios.get('/users'); //* /users 로 get요청하자.
      const users = res.data; //*응답이 올텐데 그 data를 users에 담자.
      const list = document.getElementById('list'); //*list에 태그객체를 넘기자.
      list.innerHTML = ''; //* 일단 내용은 비워두자.
      // 사용자마다 반복적으로 화면 표시 및 이벤트 연결
      Object.keys(users).map(function (key) {//* key값 중 users만 추려서 map을 돌리자. 반복문 고고
        const userDiv = document.createElement('div'); //* 태그 생성
        const span = document.createElement('span'); //* 태그 생성
        span.textContent = users[key]; //*span태그에 users의 이번 반복문의 해당 key값을 넣자.
        const edit = document.createElement('button');//* 버튼을 만들자.
        edit.textContent = '수정'; //* 내용을 수정이라하자.
        edit.addEventListener('click', async () => { //* 수정버튼 클릭하면
          const name = prompt('바꿀 이름을 입력하세요');
          if (!name) { //* 값이 없으면
            return alert('이름을 반드시 입력하셔야 합니다');
          }
          try {
            await axios.put('/user/' + key, { name }); //* put으로 /users/ key name을 전송하자.
            getUser();//* put방식으로 서버에 요청했으니 이번에는 다시 응답을 받기 위해 재귀호출하자.
          } catch (err) { //* 에러나면 에러 표시
            console.error(err);
          }
        });
        //* 아직 map이 돌고 있다.
        const remove = document.createElement('button'); //* 버튼을 만들자.
        remove.textContent = '삭제';//*삭제라고 버튼 이름을 짓자.
        remove.addEventListener('click', async () => { // 삭제 버튼 클릭
          try {
            await axios.delete('/user/' + key);//* delete 방식으로 서버에 값을 요청하자.
            getUser(); //*서버에 보냈으니 또 재귀호출해서 응답을 보자.
          } catch (err) {
            console.error(err);
          }
        });
        //* 아직 map이 돌고 있다.
        userDiv.appendChild(span);//*div에 자식으로 span을 넣자.
        userDiv.appendChild(edit);//*div에 자식으로  edit를 넣자.
        userDiv.appendChild(remove);//*div에 자식으로 remove를 넣자.
        list.appendChild(userDiv);//*div에 자식으로 userDiv를 넣자.
        //* 위 4줄은 모두 HTML 엘리먼트 들이다.
        console.log(res.data);
      });
    } catch (err) { //*통신이 에러나면
      console.error(err); //*에러를 뿌리자.
    }
  }
  


  
  window.onload = getUser; //* 화면 로딩 시 getUser 호출해서 미리 돌려놓자.
  // 폼 제출(submit) 시 실행
  document.getElementById('form').addEventListener('submit', async (e) => {//* 등록버튼을 누를 때까지 기다리는 거다.
    e.preventDefault(); //*이벤트를 보호하는 것 같다.
    const name = e.target.username.value; //*이벤트의 username의 값을 name에 넣자.
    if (!name) { //* name이 빈값이면 알리자.
      return alert('이름을 입력하세요');
    }
    try {//* post방식으로 /user url에 name을 넣자.
      await axios.post('/user', { name });
      getUser(); //*보냈으니 getUser함수를 호출하자.
    } catch (err) { //* 통신에러면 에러를 주자.
      console.error(err);
    }
    e.target.username.value = ''; //* 이벤트 객체에 넣을 디스패치할 target의 username속성값을 비워두자.
  });