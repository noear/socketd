
from abc import ABC, abstractmethod
from io import BytesIO
from typing import Type, TypeVar, Callable

from socketd.transport.core.codec.Buffer import Buffer
from socketd.transport.core.Frame import Frame

In = TypeVar("In", bound=Type)
Out = TypeVar("Out", bound=Type)


class Codec(ABC):
    """
    编解码器
    """

    @abstractmethod
    def read(self, buffer: Buffer) -> Frame:
        """
        编码
        """
        pass

    @abstractmethod
    def write(self, frame, target: Callable) -> 'CodecWriter':
        """
        解码
        """
        pass


class CodecReader(ABC):
    @abstractmethod
    def get_bytes(self, size) -> bytes: ...

    @abstractmethod
    def get_int(self) -> int: ...

    @abstractmethod
    def skip_bytes(self, size): ...

    @abstractmethod
    def seek(self, size): ...

    @abstractmethod
    def position(self): ...

    @abstractmethod
    def remaining(self) -> int: ...

    @abstractmethod
    def get_buffer(self) -> BytesIO: ...

    @abstractmethod
    def close(self): ...


class CodecWriter(ABC):
    @abstractmethod
    def put_bytes(self, _bytes: bytearray | memoryview | bytes): ...

    @abstractmethod
    def put_int(self, _num: int): ...

    @abstractmethod
    def put_char(self, _val):...

    @abstractmethod
    def flush(self): ...

    @abstractmethod
    def get_buffer(self) -> Buffer: ...

    @abstractmethod
    def close(self): ...
