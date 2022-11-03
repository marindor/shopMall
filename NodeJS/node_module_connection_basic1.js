const http = require('http');
const fs = require('fs').promises;

const users = {}; // 데이터 저장용

http.createServer(async (req, res) => { //*비동기 방식으로 통신하겠다.
  try {
    if (req.method === 'GET') { //*get방식의 요청이면
      if (req.url === '/') { //*url이 '/'이면
        const data = await fs.readFile('./restFront.html'); //*restFront.html을 읽어서 data에 담아라.
        res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });//*응답에 200과 함께 res의 헤더에 세팅해라
        return res.end(data); //*res 객체에 request받은 data를 바인딩해라.
      } else if (req.url === '/about') { //*요청이 /about이라면
        const data = await fs.readFile('./about.html'); //*about.html을 읽어서 data에 저장해라.
        res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' }); //*상동
        return res.end(data); //*상동
      } else if (req.url === '/users') { //*url이 /users이면
        res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' }); //*응답헤더에 세팅해라.
        return res.end(JSON.stringify(users)); //*상기 선언한 users 객체를 제이슨화 시켜서 응답에 담아라.
      }
      // /도 /about도 /users도 아니면
      try {
        const data = await fs.readFile(`.${req.url}`); //*url이 위 경우가 아니면 에러를 줘라.
        return res.end(data);
      } catch (err) {
        // 주소에 해당하는 라우트를 못 찾았다는 404 Not Found error 발생
      }
    } else if (req.method === 'POST') { //*요청이 post 방식이면
      if (req.url === '/user') { //*요청이 /user로 오면
        let body = '';
        // 요청의 body를 stream 형식으로 받음
        req.on('data', (data) => { //*data를 req객체에 바인딩하자.
          body += data; //*body 내용에 data를 넣어라.
        });
        // 요청의 body를 다 받은 후 실행됨
        return req.on('end', () => { //*end 이벤트가 걸리면
          console.log('POST 본문(Body):', body);
          const { name } = JSON.parse(body); //*body내용을 json화 시켜서 name만 골라서 넣어라.
          const id = Date.now();
          users[id] = name; //*users의 key값인 id에 value값인 name을 넣어라.
          res.writeHead(201, { 'Content-Type': 'text/plain; charset=utf-8' });//*응답에 201쓰고 헤더 세팅해라.
          res.end('ok');//*res에 end 이벤트 때 'ok'를 써라.
        });
      }
    } else if (req.method === 'PUT') { //*요청이 put 방식이면
      if (req.url.startsWith('/user/')) { //*urs이 /users/라면
        const key = req.url.split('/')[2]; //*  '/'로 쪼개서 3번쨰 값을 key에 넣어라
        let body = ''; //*일단 내용인 body는 데이터가 없다.
        req.on('data', (data) => { //*req에 data가 들어오면 바인딩하라.
          body += data;
        });
        return req.on('end', () => { //*응답을 다 받아서 end 이벤트가 걸리면
          console.log('PUT 본문(Body):', body);
          users[key] = JSON.parse(body).name; //*key값에 json화 시킨 name을 넣어라.
          res.writeHead(200, { 'Content-Type': 'text/plain; charset=utf-8' });//*응답에 200넣고 헤더 세팅해라.
          return res.end('ok');//*응답 끝났을 때 이벤트로 'ok'써라.
        });
      }
    } else if (req.method === 'DELETE') {//*delete 로 요청이 오면
      if (req.url.startsWith('/user/')) {//*/user/로 url이 들어오면
        const key = req.url.split('/')[2];//*내용을 '/'기준으로 쪼개서 3번째 값을 key에 넣어라.
        delete users[key];//* 기존 users의 key에 해당하는 내용을 지워라.
        res.writeHead(200, { 'Content-Type': 'text/plain; charset=utf-8' });//*응답에 200넣고 헤더 세팅해라.
        return res.end('ok');//* 응답 끝날 때 'ok'줘라.
      }
    }
    //*이도저도 아니면
    res.writeHead(404); //*헤더에 404 넣어라.
    return res.end('NOT FOUND');//응답 끝날 때 'not found'줘라.

  } catch (err) { //통신 에러가 발생시
    console.error(err);
    res.writeHead(500, { 'Content-Type': 'text/plain; charset=utf-8' });//*응답 헤더 세팅하고
    res.end(err.message);//*err메세지를 고스란히 줘라.
  }
})
  .listen(8082, () => { //8082번에서 계속 통신 대기해라.
    console.log('8082번 포트에서 서버 대기 중입니다');
  });