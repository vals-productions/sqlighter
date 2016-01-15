package com.vals.a2ios.mobilighter.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class MUtil {

	public static String actualUrl(String baseUrl, String paramString) {
        StringBuilder sb = new StringBuilder(baseUrl);
        if (!MUtil.isEmpty(paramString)) {
            sb.append('?');
            if(paramString.startsWith("&")) {
                sb.append(paramString.substring(1));
            } else {
                sb.append(paramString);
            }
        }
        return sb.toString();
    }

	/**
	 * Is d1 newer than d2
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isNewer(Date d1, Date d2) {
		if (d1 == null && d2 == null) {
			return false;
		}
		if(d1 != null && d2 == null) {
			return true;
		}
		if(d1.after(d2)) {
			return true;
		}
		return false;
	}

	public static String concatenate(String[] c) {
		if (c == null) {
			return null;
		}
		List<String> l = Arrays.asList(c);
		return concatenate(l);
	}

	public static String concatenate(Collection<String> c) {
		StringBuilder sb = new StringBuilder();
		for (String s: MUtil.emptyIfNull(c)) {
			sb.append(s); sb.append(' ');
		}
		return sb.toString();
	}

	public static String mapToUrl(Map<String, Object> map) {
		Set<String> keySet = map.keySet();
		for (String key: keySet) {
			Object value = map.get(key);
			if (value != null) {
				addNotNullValue(map, key, value);
			}
		}
		return getParametersStr(map);
	}

	public static void addNotNullValue(Map<String, Object> map, String name, Object value) {
		if (value != null) {
			map.put(name, value.toString());
		}
	}

	public static String getParametersStr(Map<String, Object> paramMap) {
		if (paramMap.size() > 0) {
			StringBuilder buf = new StringBuilder();
			Set<String> keys = paramMap.keySet();
			for (String key: keys) {
				buf.append("&");
				buf.append(key).append("=").append(paramMap.get(key));
			}
			return buf.toString();
		}
		return "";
	}

    /**
    * rename to single
    */
	public static <T> T getFirstElementOrNull(Collection<T> coll) {
		if(isEmpty(coll) || coll.size() > 1) {
			return null;
		}
		return coll.iterator().next();
	}
	
    public static <T> Collection<T> emptyIfNull(T a[]) {
        if (a == null) {
            return Collections.emptyList(); 
        }
        List<T> l = Arrays.asList(a);
        return emptyIfNull(l);
    } 
    
    public static <T> Collection<T> emptyIfNull(Collection<T> c) {
        return ((c == null) ? Collections.<T>emptyList() : c);
    }

	public static boolean isEmpty(Collection<?> c) {
		if (c != null && c.size() > 0) {
			return false;
		}
		return true;
	}

	public static boolean isEqualTo(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		}
		if (s1 == null || s2 == null) {
			return false;
		}
		return s1.equals(s2);
	}

	public static boolean isG(Number n1, Number n2) {
	    if (n1 == null && n2 == null) {
	        return false;
	    }
	    if (n1 == null || n2 == null) {
	        return false;
	    }
	    return n1.doubleValue() > n2.doubleValue();
	}

    public static boolean isEqualTo(Number o1, Number value) {
        return isEqualTo(o1, value, null);
    }

	public static boolean isEqualTo(Number o1, Number value, Double allowance) {
        if (o1 == null && value == null) {
            return true;
        }
        if (o1 == null || value == null) {
            return false;
        }
		Double v1 = o1.doubleValue();
		Double v2 = value.doubleValue();
		if (v1 == null && v2 == null) {
		    return true;
		}
		if (v1 == null || v2 == null) {
		    return false;
		}
		boolean eq;
		if(allowance == null) {
		    eq = v1.equals(v2); 
		} else {
		    double delta = v1 - v2;
		    eq = Math.abs(delta) < allowance;
		}
		return eq;
	}

	public static boolean isEmpty(String s1) {
		if (s1 == null || "".equals(s1.trim())) {
			return true;
		}
		return false;
	}

	private static String emptyIfNull(String str) {
	    if(str == null) {
	        return "";
	    }
	    return str;
	}

	public static boolean containsAnyChar(String strToCheck, String charSeq) {
	    for(int i = 0; i < charSeq.length(); i++) {
	        //String cs = new String(charSeq.charAt(i));
	        if(strToCheck.contains("" + charSeq.charAt(i))) {
	            return true;
	        }
	    }
	    return false;
	}


	public static Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }  else if(value instanceof String) {
            try {
                value = (String)value;
                return Double.parseDouble((String)value);
            } catch (NumberFormatException nfe) {
                return null;
            }
        } else if (value instanceof Boolean) {
            return Boolean.TRUE.equals(value) ? 1.0: 0.0;
        }
        return null;
	}

    public static Integer toInt(Object value) {
        Long l = toLong(value);
        if(l == null) {
            return null;
        }
        return l.intValue();
    }	
	
	public static Long toLong(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
		    return ((Number) value).longValue();
		}  else if(value instanceof String) {
			try {
			    value = (String)value;
				return Long.parseLong((String)value);
			} catch (NumberFormatException nfe) {
				return null;
			}
		} 
		return null;
	}
	
	public static Number toNotNull(Number value) {
			return (value == null) ? 0 : value;
	}
	public static String toNotNull(String value) {
		return (value == null) ? "" : value;
	}

}
