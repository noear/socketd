
from socketd.transport.core import Listener
from socketd.transport.core.Message import Message
from socketd.transport.core.Session import Session


class PipelineListener(Listener):

    def __init__(self):
        self._deque: list[Listener] = []

    def prev(self, listener: Listener) -> 'PipelineListener':
        self._deque.insert(0, listener)
        return self

    def next(self, listener: Listener) -> 'PipelineListener':
        self._deque.append(listener)
        return self

    def size(self) -> int:
        return self._deque.__len__()

    async def on_open(self, session: Session):
        for listener in self._deque:
            await listener.on_open(session)

    async def on_message(self, session: Session, message: Message):
        for listener in self._deque:
            await listener.on_message(session, message)

    async def on_close(self, session: Session):
        for listener in self._deque:
            await listener.on_close(session)

    def on_error(self, session: Session, error:Exception):
        for listener in self._deque:
            listener.on_error(session, error)




