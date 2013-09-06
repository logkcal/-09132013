package com.henry4j;

public class Problem {
//    public static class BNode<T extends Comparable<T>> implements Comparable<BNode<T>> {
//        public static <T extends Comparable<T>> List<BNode<T>> yieldPreorder(BNode<T> current) {
//            List<BNode<T>> output = new ArrayList<BNode<T>>();
//            Stack<BNode<T>> stack = new Stack<BNode<T>>();
//
//            while (null != current || !stack.isEmpty()) {
//                if (null == current) {
//                    current = stack.pop();
//                } else {
//                    output.add(current);
//                    stack.push(current.right);
//                    current = current.left;
//                }
//            }
//
//            return output;
//        }
//
//        public static <T extends Comparable<T>> List<BNode<T>> yieldPostorder(BNode<T> current) {
//            List<BNode<T>> output = new ArrayList<BNode<T>>();
//            Stack<BNode<T>> stack = new Stack<BNode<T>>();
//
//            while (null != current || !stack.isEmpty()) {
//                if (null == current) {
//                    while (!stack.isEmpty() && null == stack.peek().right) {
//                        current = stack.pop();
//                        output.add(current);
//                    }
//
//                    current = stack.isEmpty() ? null : stack.peek().right;
//                } else {
//                    stack.push(current);
//                    current = current.left;
//                }
//            }
//
//            return output;
//        }
//    }
//
//    public static class TicTacToeEngine {
//        public enum TicTacToePiece { None, X, O; }
//        public enum TicTacToeState { Uninitialized, Initialized, InProgress, Over; }
//        public enum TicTacToeEngineResponse { IllegalPiece, IllegalPosition, GameOver, GameInProgress }
//
//        private TicTacToeState gameState = TicTacToeState.InProgress;
//        private TicTacToePiece winningPiece = TicTacToePiece.None;
//        private TicTacToePiece currentPiece = TicTacToePiece.None;
//        private TicTacToePiece[] boardArray = new TicTacToePiece[9];
//        private final static int[][] boardLines = new int[][] {
//            {0, 1}, {3, 1}, {6, 1}, // horizontal lines
//            {0, 3}, {1, 3}, {2, 3}, // vertical lines
//            {0, 4}, {2, 2} }; // diagonal lines
//
//        public TicTacToeEngine(TicTacToePiece firstPiece) { initialize(firstPiece); }
//        public TicTacToeEngineResponse reset(TicTacToePiece firstPiece) { initialize(firstPiece); return null; }
//        private void initialize(TicTacToePiece firstPiece) {
//            this.boardArray = new TicTacToePiece[9];
//            this.currentPiece = firstPiece;
//            this.gameState = TicTacToeState.Initialized;
//            this.winningPiece = TicTacToePiece.None;
//        }
//
//        public TicTacToeEngineResponse movePiece(TicTacToePiece piece, int offset) {
//            if (gameState == TicTacToeState.Over) {
//                return TicTacToeEngineResponse.GameOver;
//            }
//
//            if (piece != currentPiece) {
//                return TicTacToeEngineResponse.IllegalPiece;
//            }
//
//            if (offset < 0 || offset > 9 || boardArray[offset] != TicTacToePiece.None) {
//                return TicTacToeEngineResponse.IllegalPosition;
//            }
//
//            boardArray[offset] = currentPiece;
//            updateGameState();
//
//            if (gameState == TicTacToeState.Over) {
//                return TicTacToeEngineResponse.GameOver;
//            } else {
//                return TicTacToeEngineResponse.GameInProgress;
//            }
//        }
//
//        public void updateGameState() {
//            for (int i = 0; i < boardLines.length; i++) {
//                if (3 == countPiecesUsingSteps(boardLines[i][0], boardLines[i][1], this.currentPiece)) {
//                    winningPiece = currentPiece;
//                }
//            }
//
//            gameState = TicTacToeState.Over;
//            if (winningPiece == TicTacToePiece.None) {
//                for (int i = 0; i < 9; i++) {
//                    if (boardArray[i] == TicTacToePiece.None) {
//                        gameState = TicTacToeState.InProgress;
//                        break;
//                    }
//                }
//            }
//
//            if (gameState == TicTacToeState.InProgress) {
//                currentPiece = (currentPiece == TicTacToePiece.X) ? TicTacToePiece.O : TicTacToePiece.X;
//            }
//        }
//
//        private int countPiecesUsingSteps(int offset, int step, TicTacToePiece current) {
//            for (int count = 0; count < 3; count++, offset += step) {
//                if (current != boardArray[offset]) {
//                    return count;
//                }
//            }
//
//            return 3;
//        }
//    }
//
//    public static String ToString(int num) { // itoa in C/C++ runtime time library
//        boolean negative = false;
//        if (num < 0) {
//            num = -num;
//            negative = true;
//        }
//
//        StringBuilder sb = new StringBuilder();
//        while (num > 0) {
//            sb.insert(0, (char)('0' + num % 10));
//            num /= 10;
//        }
//
//        if (negative) {
//            sb.insert(0, '-');
//        }
//
//        return sb.toString();
//    }
//
//    public static int parse(String str) {
//        int num = 0;
//        boolean negative = false;
//        int i = 0;
//        if (str.charAt(i) == '-') {
//            i++; negative = true;
//        }
//
//        for (; i < str.length(); i++) {
//            if (str.charAt(i) < '0' && str.charAt(i) > '9') throw new IllegalArgumentException("Invalid format");
//            num *= 10;
//            num += (str.charAt(i) - '0');
//        }
//
//        return negative ? -num : num;
//    }
//
//    public static int LastIndexOfAny(String s, char... anyOf) {
//        Map<Character, Object> map = new HashMap<Character, Object>();
//        for (int i = 0; i < anyOf.length; i++) {
//            map.put(anyOf[i], null);
//        }
//
//        for (int i = s.length() - 1; i > 0; i--) {
//            if (map.containsKey(s.charAt(i))) return i;
//        }
//
//        return -1;
//    }
//
////    public static IEnumerable<string> Tokenize(string str, string delimiters) {
////        System.Collections.BitArray map = new System.Collections.BitArray(256); // ASCII
////        for (int i = 0; i < delimiters.Length; i++) map[delimiters[i]] = true;
////        for (int head = 0; head < str.Length; head++)
////        {
////            if (map[str[head]]) continue;
////            int tail = head + 1;
////            while (tail < str.Length && map[str[tail]] == false) tail++;
////            yield return str.Substring(head, tail - head);
////        }
////    }
//
//    public static class BitOperations {
//        public static void swap(int[] a) {
//            a[0] ^= a[1] ^= a[0] ^= a[1];
//        }
//
////      public static int CountOnes(int value) {
////          unchecked {
////              uint x = (uint)value;
////              x = ((0xaaaaaaaa & x) >> 1) + (0x55555555 & x);
////              x = ((0xcccccccc & x) >> 2) + (0x33333333 & x);
////              x = ((0xf0f0f0f0 & x) >> 4) + (0x0f0f0f0f & x);
////              x = ((0xff00ff00 & x) >> 8) + (0x00ff00ff & x);
////              x = (x >> 16) + (0x0000ffff & x);
////              return (int)x;
////          }
////      }
//
////      public static int Reverse(int value) {
////          unchecked {
////              uint x = (uint)value;
////              x = x >> 16 | (0x0000ffff & x) << 16;
////              x = (0xff00ff00 & x) >> 8 | (0x00ff00ff & x) << 8;
////              x = (0xf0f0f0f0 & x) >> 4 | (0x0f0f0f0f & x) << 4;
////              x = (0xcccccccc & x) >> 2 | (0x33333333 & x) << 2;
////              x = (0xaaaaaaaa & x) >> 1 | (0x55555555 & x) << 1;
////              return (int)x;
////          };
////      }
//
////      public static int CountTrailingZeros(int value) {
////          return CountOnes((value & -value) - 1);
////      }
//
////      public static int CountLeadingZeros(int value) {
////          unchecked {
////              uint x = (uint)value;
////              x |= x >>= 1;
////              x |= x >>= 2;
////              x |= x >>= 4;
////              x |= x >>= 8;
////              x |= x >>= 16;
////              return CountOnes((int)~x);
////          }
////      }
//    }
}
