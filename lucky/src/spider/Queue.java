package spider;

import java.util.LinkedList;

/**
 * �Զ�������� ����TODO��
 */
public class Queue {
	/**
	 * ����һ�����У�ʹ��LinkedListʵ��
	 */
	private LinkedList<Object> queue = new LinkedList<Object>(); // �����

	/**
	 * ��t���뵽������
	 */
	public void enQueue(Object t) {
		queue.addLast(t);
	}

	/**
	 * �Ƴ������еĵ�һ����䷵��
	 */
	public Object deQueue() {
		return queue.removeFirst();
	}

	/**
	 * ���ض����Ƿ�Ϊ��
	 */
	public boolean isQueueEmpty() {
		return queue.isEmpty();
	}

	/**
	 * �жϲ����ض����Ƿ����t
	 */
	public boolean contians(Object t) {
		return queue.contains(t);
	}

	/**
	 * �жϲ����ض����Ƿ�Ϊ��
	 */
	public boolean empty() {
		return queue.isEmpty();
	}
}